package ui.pages;

import com.codeborne.selenide.Selenide;

public abstract class BasePage<T extends BasePage> {
    // Каждая страница должна указать свой URL - реализовать абстр.метод
    public abstract String url();

    /**
     * Открывает страницу в браузере по URL из метода url()
     * @return экземпляр страницы (тип T) для дальнейших действий
     */
    public T open() {
        return Selenide.open(url(), (Class<T>) this.getClass());
    }

    /**
     * Переключается на другую страницу без перезагрузки браузера
     * @param pageClass - класс нужной страницы
     * @return объект запрошенной страницы
     */
    public <T extends BasePage> T getPage(Class<T> pageClass) {
        return Selenide.page(pageClass);
    }
}