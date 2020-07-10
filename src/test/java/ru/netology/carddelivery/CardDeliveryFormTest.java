package ru.netology.carddelivery;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryFormTest {
    private int daysOffset = 237;
    private String cityDelivery = "Новосибирск";
    private LocalDateTime orderDate = LocalDateTime.now().plusDays(daysOffset);
    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private String getOrderDateEpochString(LocalDateTime dateTime) {
        Date dt = null;
        try {
            dt = new SimpleDateFormat("dd.MM.yyyy").parse(dateFormat.format(dateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(dt.getTime());
    }

    /*
    arrowIndex:
        1 - month's left arrow "-1"
        2 - year's right arrow "12"
        3 - month's right arrow "1"
     */
    private void calendarArrowClick(int arrowIndex) {
        $$(".calendar__arrow").get(arrowIndex).click();
    }

    /*
    data-step value:
            "-1" - month's left arrow
            "12" - year's right arrow "12"
            "1" - month's right arrow "1"
    */
    private void calendarArrowClick(String dataStep) {
        $$(".calendar__arrow")
                .findBy(Condition.attribute("data-step", dataStep)).click();
    }

    private void rightArrowYearClick() {
        calendarArrowClick(2);
    }

    private void rightArrowMonthClick() {
        calendarArrowClick(3);
    }

    private void leftArrowMonthClick() {
        calendarArrowClick(1);
    }

    private int getMonthArrowClickCount() {
        return orderDate.getMonth().getValue() - LocalDateTime.now().getMonth().getValue();
    }

    private int getYearArrowClickCount() {
        return orderDate.getYear() - LocalDateTime.now().getYear();
    }

    private void selectYearMonth(int yearArrowClickCount, int monthArrowClickCount) {
        int repeat = 0;
        while (repeat < yearArrowClickCount) {
            rightArrowYearClick();
            repeat++;
        }
        repeat = 0;
        if (monthArrowClickCount < 0) {
            while (repeat > monthArrowClickCount) {
                leftArrowMonthClick();
                repeat--;
            }
        } else {
            while (repeat < monthArrowClickCount) {
                rightArrowMonthClick();
                repeat++;
            }
        }
    }

    @Test
    public void shouldCreditCardDeliveryOrderByText() {
        open("http://localhost:9999");

        SelenideElement calendar = $(".calendar");
        SelenideElement element = $("form");
        element.$("[data-test-id=city] input").setValue("Но");
        $(byText(cityDelivery)).click();
        element.$("[data-test-id=date] input").click();

        selectYearMonth(getYearArrowClickCount(), getMonthArrowClickCount());

        calendar.$(byText(String.valueOf(orderDate.getDayOfMonth()))).click();
        element.$("[data-test-id=name] input").setValue("Иванов Петр Петрович");
        element.$("[data-test-id=phone] input").setValue("+79099099090");
        element.$("[data-test-id=agreement]").click();
        element.$$("button").find(exactText("Забронировать")).click();

        $(withText("Успешно!")).waitUntil(visible, 15000);
        $(byText("Встреча успешно забронирована на")).shouldBe(visible);
        $(byText(dateFormat.format(orderDate))).shouldBe(visible);
    }

    @Test
    public void shouldCreditCardDeliveryOrderByCss() {
        open("http://localhost:9999");

        SelenideElement calendar = $(".calendar");
        SelenideElement element = $("form");
        element.$("[data-test-id=city] input").setValue("Но");
        $(byText(cityDelivery)).click();
        element.$("[data-test-id=date] input").click();

        selectYearMonth(getYearArrowClickCount(), getMonthArrowClickCount());

        ElementsCollection calendarRows = calendar.$$(".calendar__row .calendar__day");
        calendarRows.findBy(Condition.attribute("data-day",
                getOrderDateEpochString(orderDate))).click();

        element.$("[data-test-id=name] input").setValue("Иванов Петр Петрович");
        element.$("[data-test-id=phone] input").setValue("+79099099090");
        element.$("[data-test-id=agreement]").click();
        element.$$("button").find(exactText("Забронировать")).click();

        $(withText("Успешно!")).waitUntil(visible, 15000);
        $(byText("Встреча успешно забронирована на")).shouldBe(visible);
        $(byText(dateFormat.format(orderDate))).shouldBe(visible);
    }
}
