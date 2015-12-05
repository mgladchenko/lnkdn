package com.linkedin.rally;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Date;

public class RallyAnnotationListener implements IInvokedMethodListener {
    public static final Logger LOG = LoggerFactory.getLogger(RallyAnnotationListener.class);

    private RallyAPI rally;

    public RallyAnnotationListener() {
        rally = new RallyAPI();
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            ITestNGMethod testNGMethod = method.getTestMethod();
            String methodPath = testNGMethod.getRealClass().getName() + "." + testNGMethod.getMethodName();
            LOG.info("\n===============");
            LOG.info("\nTEST \"" + methodPath + "\" has been started.");
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        //System.out.println("");
        if (method.isTestMethod()) {
            String testStatus = "?";

            switch (testResult.getStatus()) {
                case 1:
                    testStatus = "PASSED";
                    break;
                case 2:
                    testStatus = "FAILURE";
                    break;
                case 3:
                    testStatus = "SKIP";
                    break;
                case 4:
                    testStatus = "SUCCESS_PERCENTAGE_FAILURE";
                    break;
                case 16:
                    testStatus = "STARTED";
                    break;
                default:
                    LOG.info("\n[WARNING] Not common testStatus. TestStatus is: " + testResult.getStatus());
                    break;
            }
            LOG.info("\nResult: " + testStatus);



            Rally rallyAnnotation = getTestAnnotation(method);
            if (rallyAnnotation != null )  {
                String testCase = getFromAnnotationOrMethodParams(rallyAnnotation.caseID(), testResult);
                int testRun = Integer.parseInt(getFromAnnotationOrMethodParams(rallyAnnotation.runID(), testResult));
                if (testResult.getStatus() == ITestResult.SUCCESS) {
                    sendTestStatus(testRun, testCase, method, "Pass", testResult);
                    LOG.info("\n[INFO] Test result was sent to Rally: TestRun - " + testRun + ", TestCase - " + testCase);
                    
                    return;
                } else if (testResult.getStatus() == ITestResult.SKIP) {
                    sendTestStatus(testRun, testCase, method, "Blocked", testResult);
                    LOG.info("\n[INFO] Test result was sent to Rally: TestRun - " + testRun + ", TestCase - " + testCase);
                    return;
                }
                sendTestStatus(testRun, testCase, method, "Fail", testResult);
                LOG.info("\n[INFO] Test result was sent to Rally: TestRun - " + testRun + ", TestCase - " + testCase);
            } else {
                //commented since we have also TestRail annotation
                //LOG.info("[WARNING] There is not Rally references! Please add one.");
            }
            LOG.info("\n===============");
        }
    }

    private String getFromAnnotationOrMethodParams(final String annotationValue, final ITestResult testResult) {
        if (annotationValue != null && annotationValue.startsWith("${")) {
            try {
                final int position = Integer.parseInt(annotationValue.substring(2, annotationValue.length() - 1));
                return (String) testResult.getParameters()[position];
            } catch (Exception e) {
                LOG.error("Invalid annotation", e);
            }
        }
        return annotationValue;
    }

    private void sendTestStatus(int testRun, String testCase, IInvokedMethod method, String testStatus, final ITestResult testResult) {
        ITestNGMethod testNGMethod = method.getTestMethod();
        String methodPath = testNGMethod.getRealClass().getName() + "." + testNGMethod.getMethodName();
        String testComment = "Test was runned automatically" +
                "\nMethod: " + methodPath +
                "\nDate: " + new Date()+
                (testResult.getThrowable() != null ? "\nError details: " + Throwables.getStackTraceAsString(testResult.getThrowable()) : "");
        try {
            rally.setTestResult(testRun, testCase, testComment, testStatus);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private Method getReflectionMethod(IInvokedMethod method) {
        String methodName = method.getTestMethod().getMethodName();
        Method[] methods = method.getTestMethod().getRealClass().getMethods();
        for (Method rMethod : methods) {
            if (rMethod.getName().equals(methodName)) {
                return rMethod;
            }
        }
        return null;
    }

    private Rally getTestAnnotation(IInvokedMethod method) {
        if (method.isTestMethod() || method.isConfigurationMethod()) {
            Method rMethod = getReflectionMethod(method);
            if (rMethod.isAnnotationPresent(Rally.class)) {
                Annotation testRailAnnotation = rMethod.getAnnotation(Rally.class);
                if (testRailAnnotation instanceof Rally) {
                    return (Rally) testRailAnnotation;
                } else {
                    return null;
                }
            }
        }
        return null;
    }


}