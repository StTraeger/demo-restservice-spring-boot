package com.github.sttraeger.sqsdemoserver.repository;

import com.github.sttraeger.sqsdemoserver.model.Car;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author sttraeger
 * Simple implemenation of a car repository. Data is stored in an ArrayList. For testing purposes only.
 */
@Component
public class SimpleCarRepository implements ICarRepository {

    private static final List<Car> cars = new ArrayList<>();

    /**
     * Creates a java.util.Date object using the java.util.Calendar class.
     *
     * @param year
     * @param month
     * @param dayOfMonth
     * @return
     */
    private Date createDate(final int year, final int month, final int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return cal.getTime();
    }

    public SimpleCarRepository() {
        cars.add(new Car("123456", "BMW", "X1", 150, createDate(2018, 4, 30), 200));
        cars.add(new Car("456123", "Fiat", "Punto", 69, createDate(2012, 10, 11), 20000));
        cars.add(new Car("654321", "Ford", "Focus", 105, createDate(1999, 10, 11), 250441));
    }

    @Override
    public Iterable<Car> getCars() {
        return cars;
    }

    @Override
    public ResponseEntity getCarByVin(String vin) {
        for (Car car : cars) {
            if (vin.equals(car.getVin())) {
                return new ResponseEntity(car, HttpStatus.OK);
            }
        }
        return null;
    }

    @Override
    public ResponseEntity createCar(String vin, Car car) {
        if (!isCarAlreadyExisting(vin)) {
            cars.add(car);
            return new ResponseEntity(car, HttpStatus.OK);
        }
        return null;
    }

    @Override
    public ResponseEntity updateExistingCar(Car car) {
        if (!isCarAlreadyExisting(car.getVin())) {
            return null;
        }
        for (Car actualCar : cars) {
            if (actualCar.getVin().equals(car.getVin())) {
                cars.remove(actualCar);
                cars.add(car);
            }
        }
        return new ResponseEntity(car, HttpStatus.OK);
    }

    @Override
    public boolean deleteCar(String vin) {
        if (!isCarAlreadyExisting(vin)) {
            return false;
        }
        for (Car actualCar : cars) {
            if (actualCar.getVin().equals(vin)) {
                cars.remove(actualCar);
            }
        }
        return true;
    }

    /**
     * Checks wether a car to a given vin already exists in data-storage.
     *
     * @param vin the vin to check
     * @return true if car already exists in db, false instead
     */
    private boolean isCarAlreadyExisting(final String vin) {
        for (Car car : cars) {
            if (car.getVin().equals(vin)) {
                return true;
            }
        }
        return false;
    }
}
