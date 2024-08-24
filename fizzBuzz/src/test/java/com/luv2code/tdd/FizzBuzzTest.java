package com.luv2code.tdd;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FizzBuzzTest {
    //If number is divisible by 3, print fizz
    //if number is divisible by 5, print buzz
    //if number is divisible by 3 and 5, print fizzBuzz
    //if number is not divisible by 3 and 5, print number

    @DisplayName("Divisible by 3")
    @Test
    @Order(1)
    void testDivisibleByThree(){
        //Stub
        //fail("fail");
        String expected = "Fizz";
        assertEquals(expected, FizzBuzz.compute(3), "Should return Fizz");
    }

    @DisplayName("Divisible by 5")
    @Test
    @Order(2)
    void testDivisibleByFive(){
        //Stub
        //fail("fail");
        String expected = "Buzz";
        assertEquals(expected, FizzBuzz.compute(50), "Should return Buzz");
    }

    @DisplayName("Divisible by 3 and 5")
    @Test
    @Order(3)
    void testDivisibleByThreeAndFive(){
        //Stub
        //fail("fail");
        String expected = "FizzBuzz";
        assertEquals(expected, FizzBuzz.compute(45), "Should return FizzBuzz");
    }

    @DisplayName("Not Divisible by 3 and 5")
    @Test
    @Order(4)
    void testNotDivisibleByThreeAndFive(){
        //Stub
        //fail("fail");
        String expected = "43";
        assertEquals(expected, FizzBuzz.compute(43), "Should return same value");
    }

    @DisplayName("Testing with Small data file")
    @ParameterizedTest(name="value={0}, expected={1}")
    @CsvFileSource(resources = "/sample-test-data.csv")
    @Order(5)
    void testSmallDataFile(int value, String expected){
        assertEquals(expected, FizzBuzz.compute(value));
    }

    @DisplayName("Testing with Medium data file")
    @ParameterizedTest(name="value={0}, expected={1}")
    @CsvFileSource(resources = "/medium-test-data.csv")
    @Order(6)
    void testMediumDataFile(int value, String expected){
        assertEquals(expected, FizzBuzz.compute(value));
    }

    @DisplayName("Testing with Large data file")
    @ParameterizedTest(name="value={0}, expected={1}")
    @CsvFileSource(resources = "/large-test-data.csv")
    @Order(6)
    void testLargeDataFile(int value, String expected){
        assertEquals(expected, FizzBuzz.compute(value));
    }
}
