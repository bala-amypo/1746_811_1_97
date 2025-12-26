package com.example.demo;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestResultListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        System.out.println("========== TEST SUITE STARTED ==========");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("PASSED : " + result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("FAILED : " + result.getName());
        result.getThrowable().printStackTrace();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("SKIPPED: " + result.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("========== TEST SUITE FINISHED ==========");
    }
}
