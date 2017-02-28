package com.darryncampbell.InstrumentedEMDKBarcodeTesting;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import com.darryncampbell.InstrumentedEMDKBarcodeTesting.com.symbol.emdk.barcode.test.EMDKBarcodeStub;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.ScannerInfo;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.StatusData;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    String test_friendly_name_internal = "2D Barcode Imager";
    String test_friendly_name_bluetooth = "Bluetooth Scanner";
    String test_barcode_value_string = "0123456789";
    byte[] test_barcode_value = test_barcode_value_string.getBytes();
    ScannerResults test_scan_result_positive = ScannerResults.SUCCESS;
    ScannerResults test_scan_result_negative = ScannerResults.FAILURE;
    String test_scan_timestamp = "2017-02-27 12:58:51.238";
    String test_scan_charsetName = "UTF-8";
    ScanDataCollection.LabelType test_scan_label_type = ScanDataCollection.LabelType.EAN13;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void successfulScan() throws Exception
    {
        MainActivity activity = mActivityRule.getActivity();

        EMDKBarcodeStub stub = new EMDKBarcodeStub(test_friendly_name_internal);

        //  Click the start scan button
        onView(withId(R.id.buttonStartScan))
                .perform(click());

        //  Simulate a barcode being scanned
        stub.AddScanData(test_barcode_value, test_scan_label_type, test_scan_charsetName, test_scan_timestamp);
        ScanDataCollection scanDataCollection = stub.ReportScan(test_scan_result_positive);
        activity.onData(scanDataCollection);

        //  Test that the correct data was scanned
        onView(withId(R.id.textViewData))
                .check(matches(withText(test_barcode_value_string + "\n")));

        //  Click the stop scan button
        onView(withId(R.id.buttonStopScan))
                .perform(click());
    }

    @Test
    public void unsuccessfulScan() throws Exception
    {
        MainActivity activity = mActivityRule.getActivity();

        EMDKBarcodeStub stub = new EMDKBarcodeStub(test_friendly_name_internal);

        //  Click the start scan button
        onView(withId(R.id.buttonStartScan))
                .perform(click());

        //  Simulate a barcode being scanned but the result is
        stub.AddScanData(test_barcode_value, test_scan_label_type, test_scan_charsetName, test_scan_timestamp);
        ScanDataCollection scanDataCollection = stub.ReportScan(test_scan_result_negative);
        activity.onData(scanDataCollection);

        //  Test that no data was shown
        onView(withId(R.id.textViewData))
                .check(matches(withText("")));

        //  Click the stop scan button
        onView(withId(R.id.buttonStopScan))
                .perform(click());
    }

    @Test
    public void scanWithStatusError() throws Exception
    {
        MainActivity activity = mActivityRule.getActivity();

        EMDKBarcodeStub stub = new EMDKBarcodeStub(test_friendly_name_internal);

        //  Click the start scan button
        onView(withId(R.id.buttonStartScan))
                .perform(click());

        //  Simulate the Scanner returning an error state
        StatusData sd = stub.ReportStatus(StatusData.ScannerStates.ERROR);
        activity.onStatus(sd);

        //  Test the error was reported
        onView(withId(R.id.textViewStatus))
                .check(matches(withText("Status: An error has occurred.")));

        //  Click the stop scan button
        onView(withId(R.id.buttonStopScan))
                .perform(click());
    }

    @Test
    public void scannerDisconnect() throws Exception
    {
        MainActivity activity = mActivityRule.getActivity();

        EMDKBarcodeStub stub = new EMDKBarcodeStub(test_friendly_name_bluetooth);

        //  Select the Bluetooth scanner from the UI dropdown
        //  I am cheating a bit here, usually selecting the scanner will try to enable it but that makes
        //  it a lot harder to test without a BT scanner to hand so I disabled that for BT scanners.
        ViewInteraction scannerSpinner = onView(
                Matchers.allOf(withId(R.id.spinnerScannerDevices), isDisplayed()));
        scannerSpinner.perform(click());
        ViewInteraction checkedTextView = onView(
                Matchers.allOf(withId(android.R.id.text1), withText("Bluetooth Scanner"), isDisplayed()));
        checkedTextView.perform(click());

        Boolean isDefaultScanner = false;
        Boolean isConnected = false;
        String modelNumber = "RS6000 N1";
        int scannerIndex = 0;
        ScannerInfo scannerInfo = stub.CreateScannerInfo(ScannerInfo.DeviceType.IMAGER, ScannerInfo.ConnectionType.BLUETOOTH_SSI,
                BarcodeManager.DeviceIdentifier.BLUETOOTH_IMAGER1, ScannerInfo.DecoderType.TWO_DIMENSIONAL,
                isDefaultScanner, isConnected, modelNumber, scannerIndex);
        BarcodeManager.ConnectionState connection_disconnected = BarcodeManager.ConnectionState.DISCONNECTED;
        activity.onConnectionChange(scannerInfo, connection_disconnected);

        //  Test the error was reported
        onView(Matchers.allOf(withId(R.id.textViewStatus), isDisplayed()))
                .check(matches(withText("Status: Bluetooth Scanner:DISCONNECTED")));

    }
}
