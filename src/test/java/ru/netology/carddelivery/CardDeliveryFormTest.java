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
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class CardDeliveryFormTest {
    private int daysDiff = 7;
    private String cityDelivery = "Новосибирск";

    private String GetOrderDay(int daysDiff) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd");
        return dateFormat.format(LocalDateTime.now().plusDays(daysDiff));
    }

    private String GetOrderDate(int daysDiff) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return dateFormat.format(LocalDateTime.now().plusDays(daysDiff));
    }

    private String GetOrderDateEpochString(String dateOrder) {
        Date dt = null;
        try {
            dt = new SimpleDateFormat("dd.MM.yyyy").parse(dateOrder);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long epoch = dt.getTime();

        return String.valueOf(epoch);
    }

    @Test
    public void shouldCreditCardDeliveryOrderByText() {

        open("http://localhost:9999");

        String dayOrder = GetOrderDay(daysDiff);
        String dateOrder = GetOrderDate(daysDiff);

        SelenideElement calendar = $("tbody");
        SelenideElement element = $("form");
        element.$("[data-test-id=city] input").setValue("Но");
        $(byText(cityDelivery)).click();

        element.$("[data-test-id=date] input").click();
        calendar.$(byText(dayOrder)).click();

        element.$("[data-test-id=name] input").setValue("Иванов Петр Петрович");
        element.$("[data-test-id=phone] input").setValue("+79099099090");
        element.$("[data-test-id=agreement]").click();
        element.$$("button").find(exactText("Забронировать")).click();

        $(withText("Успешно!")).waitUntil(visible, 15000);
        $(byText("Встреча успешно забронирована на")).shouldBe(visible);
        $(byText(dateOrder)).shouldBe(visible);
    }

    @Test
    public void shouldCreditCardDeliveryOrderByCss() {

        open("http://localhost:9999");

        String dateOrder = GetOrderDate(daysDiff);
        String dateEpochString = GetOrderDateEpochString(dateOrder);

        SelenideElement calendar = $("tbody");
        SelenideElement element = $("form");
        element.$("[data-test-id=city] input").setValue("Но");
        $(byText(cityDelivery)).click();

        element.$("[data-test-id=date] input").click();
        ElementsCollection el = calendar.$$(".calendar__row .calendar__day");
        SelenideElement day = el.findBy(Condition.attribute("data-day",dateEpochString));
        day.click();

        element.$("[data-test-id=name] input").setValue("Иванов Петр Петрович");
        element.$("[data-test-id=phone] input").setValue("+79099099090");
        element.$("[data-test-id=agreement]").click();

        element.$$("button").find(exactText("Забронировать")).click();
        $(withText("Успешно!")).waitUntil(visible, 15000);
        $(byText("Встреча успешно забронирована на")).shouldBe(visible);
        $(byText(dateOrder)).shouldBe(visible);
    }
}
