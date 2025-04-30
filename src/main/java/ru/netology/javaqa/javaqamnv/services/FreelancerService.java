package ru.netology.javaqa.javaqamnv.services;

public class FreelancerService {

    // Метод для расчета количества месяцев отдыха
    public int calculateRestMonths(int income, int expenses, int threshold) {
        int money = 0; // начальная сумма на счете
        int restMonths = 0;

        for (int month = 1; month <= 12; month++) {
            if (money >= threshold) { // если на счете достаточно средств, отдыхаем
                money -= expenses; // уменьшаем деньги на обязательные траты
                money /= 3; // уменьшаем остаток еще в три раза
                restMonths++; // увеличиваем количество месяцев отдыха
            } else { // иначе работаем
                money += income; // увеличиваем деньги от дохода
                money -= expenses; // уменьшаем деньги на обязательные траты
            }
        }

        return restMonths; // возвращаем количество месяцев отдыха
    }
}