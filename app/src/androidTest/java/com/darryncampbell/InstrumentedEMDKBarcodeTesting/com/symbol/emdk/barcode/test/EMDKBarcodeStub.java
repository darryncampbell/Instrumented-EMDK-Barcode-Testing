package com.darryncampbell.InstrumentedEMDKBarcodeTesting.com.symbol.emdk.barcode.test;

import android.util.Log;

import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.ScannerInfo;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.StatusData;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * Created by darry on 27/02/2017.
 * Class designed to stub out the functionality of the Zebra EMDK barcode scanner.
 * Use during automated testing on real hardware to simulate barcodes being scanned and the
 * hardware reporting various statuses
 */
public class EMDKBarcodeStub {

    private String reportedFriendlyName;
    private ArrayList<ScanDataStub> bufferedScans = new ArrayList<>();
    private static final String TAG = "Barcode Stub";
    private static final String FIELD_FRIENDLY_NAME = "friendlyName";
    private static final String FIELD_SCANNER_STATE = "scannerState";
    private static final String FIELD_RESULT = "result";
    private static final String FIELD_RAW_DATA = "rawData";
    private static final String FIELD_CHARSET_NAME = "charsetName";
    private static final String FIELD_TIMESTAMP = "timeStamp";
    private static final String FIELD_LABEL_TYPE = "labelType";
    private static final String FIELD_SCANNER_INDEX = "scannerIndex";
    private static final String FIELD_MODEL_NUMBER = "modelNumber";
    private static final String FIELD_DEVICE_TYPE = "deviceType";
    private static final String FIELD_CONNECTION_TYPE = "connectionType";
    private static final String FIELD_DEVICE_IDENTIFIER = "deviceIdentifier";
    private static final String FIELD_DECODER_TYPE = "decoderType";
    private static final String FIELD_IS_DEFAULT_SCANNER = "isDefaultScanner";
    private static final String FIELD_IS_CONNECTED = "isConnected";

    /**
     * Constructor
     * @param scannerFriendlyName Scanner friendly name.  Shared between the onStatus and onData callbacks.
     */
    public EMDKBarcodeStub(String scannerFriendlyName)
    {
        this.reportedFriendlyName = scannerFriendlyName;
    }

    /**
     * Creates a StatusData object which can be provided to the activity's onStatus() method
     * to simulate a status report from the scanner
     * @param statusToReport
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchFieldException
     * @throws NoSuchMethodException
     */
    public StatusData ReportStatus(StatusData.ScannerStates statusToReport) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException, NoSuchMethodException {
        StatusData statusData = null;
        try {
            Constructor<StatusData> scannerStatusDataConstructor = StatusData.class.getDeclaredConstructor(new Class[0]);
            scannerStatusDataConstructor.setAccessible(true);
            statusData = scannerStatusDataConstructor.newInstance(new Object[0]);
            Field fieldStatusDataFriendlyName = StatusData.class.getDeclaredField(FIELD_FRIENDLY_NAME);
            fieldStatusDataFriendlyName.setAccessible(true);
            fieldStatusDataFriendlyName.set(statusData, reportedFriendlyName);
            Field fieldStatusDataScannerState = StatusData.class.getDeclaredField(FIELD_SCANNER_STATE);
            fieldStatusDataScannerState.setAccessible(true);
            fieldStatusDataScannerState.set(statusData, statusToReport);
            }
            catch (InstantiationException e) {
                Log.e(TAG, "Problem with reflecting on Zebra EMDK API" + e.getMessage());
                throw e;
            } catch (IllegalAccessException e) {
                Log.e(TAG, "Problem with reflecting on Zebra EMDK API" + e.getMessage());
                throw e;
            } catch (InvocationTargetException e) {
                Log.e(TAG, "Problem with reflecting on Zebra EMDK API" + e.getMessage());
                throw e;
            } catch (NoSuchFieldException e) {
                Log.e(TAG, "Problem with reflecting on Zebra EMDK API" + e.getMessage());
                throw e;
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "Problem with reflecting on Zebra EMDK API" + e.getMessage());
                throw e;
            }
        return statusData;
    }

    /**
     * One trigger pull can return multiple barcodes in certain scanner modes.  Simulating a scan
     * is done in two steps, this is the first, to provide the scanned barcodes one by one via this method.
     * @param scanDataRawBytes barcodeData as byte[]
     * @param scanLabelType e.g. EAN13
     * @param charsetName e.g. UTF-8
     * @param timestamp e.g. "2017-02-27 12:58:51.238" (that is the format the scanner currently uses as far as I can see)
     */
    public void AddScanData(byte[] scanDataRawBytes, ScanDataCollection.LabelType scanLabelType,
                            String charsetName, String timestamp)
    {
        //  Add a single scan which will be buffered
        ScanDataStub temp = new ScanDataStub(scanDataRawBytes, scanLabelType, charsetName, timestamp);
        bufferedScans.add(temp);
    }

    /**
     * The second step in mimicing a barcode scan is to instruct class to parse all the data you
     * previously provided via AddScanData and return a ScanDataCollection object.  The ScanDataCollection
     * object can then be sent to your activity's onData() handler to mimic a barcode being scanned.
     * @param scannerResult
     * @return
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public ScanDataCollection ReportScan(ScannerResults scannerResult) throws InstantiationException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
        ScanDataCollection scanDataCollection = null;
        if (bufferedScans.size() == 0)
        {
            Log.e(TAG, "No scans available to report");
            throw new NoSuchFieldException("No scans available to report");
        }
        try {
            Constructor<ScanDataCollection> scanDataCollectionConstructor = ScanDataCollection.class.getDeclaredConstructor(new Class[0]);
            scanDataCollectionConstructor.setAccessible(true);
            scanDataCollection = scanDataCollectionConstructor.newInstance(new Object[0]);
            Field fieldFriendlyName = ScanDataCollection.class.getDeclaredField(FIELD_FRIENDLY_NAME);
            fieldFriendlyName.setAccessible(true);
            fieldFriendlyName.set(scanDataCollection, reportedFriendlyName);
            Field fieldScannerResult = ScanDataCollection.class.getDeclaredField(FIELD_RESULT);
            fieldScannerResult.setAccessible(true);
            fieldScannerResult.set(scanDataCollection, scannerResult);

            ArrayList<ScanDataCollection.ScanData> scannedData = new ArrayList<>();

            for (int i = 0; i < bufferedScans.size(); i++) {
                Constructor[] scanDataConstructors = ScanDataCollection.ScanData.class.getDeclaredConstructors();
                Constructor scanDataConstructor = scanDataConstructors[0];
                scanDataConstructor.setAccessible(true);
                ScanDataCollection.ScanData data = (ScanDataCollection.ScanData) scanDataConstructor.newInstance(scanDataCollection);
                Field fieldScanDataRawData = ScanDataCollection.ScanData.class.getDeclaredField(FIELD_RAW_DATA);
                fieldScanDataRawData.setAccessible(true);
                fieldScanDataRawData.set(data, bufferedScans.get(i).getScanDataRawBytes());
                Field fieldScanDataCharsetName = ScanDataCollection.ScanData.class.getDeclaredField(FIELD_CHARSET_NAME);
                fieldScanDataCharsetName.setAccessible(true);
                fieldScanDataCharsetName.set(data, bufferedScans.get(i).getCharsetName());
                Field fieldScanDataTimestamp = ScanDataCollection.ScanData.class.getDeclaredField(FIELD_TIMESTAMP);
                fieldScanDataTimestamp.setAccessible(true);
                fieldScanDataTimestamp.set(data, bufferedScans.get(i).getTimestamp());
                Field fieldScanDataLabelType = ScanDataCollection.ScanData.class.getDeclaredField(FIELD_LABEL_TYPE);
                fieldScanDataLabelType.setAccessible(true);
                fieldScanDataLabelType.set(data, bufferedScans.get(i).getScanLabelType());
                scannedData.add(data);
            }

            Field fieldScannedData = ScanDataCollection.class.getDeclaredField("scanData");
            fieldScannedData.setAccessible(true);
            fieldScannedData.set(scanDataCollection, scannedData);
        } catch (InstantiationException e) {
            Log.e(TAG, "Problem with reflecting on Zebra EMDK API" + e.getMessage());
            throw e;
        } catch (InvocationTargetException e) {
            Log.e(TAG, "Problem with reflecting on Zebra EMDK API" + e.getMessage());
            throw e;
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "Problem with reflecting on Zebra EMDK API" + e.getMessage());
            throw e;
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Problem with reflecting on Zebra EMDK API" + e.getMessage());
            throw e;
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "Problem with reflecting on Zebra EMDK API" + e.getMessage());
            throw e;
        }
        bufferedScans.clear();
        return scanDataCollection;
    }

    public ScannerInfo CreateScannerInfo(ScannerInfo.DeviceType deviceType, ScannerInfo.ConnectionType connectionType,
                                         BarcodeManager.DeviceIdentifier deviceIdentifier, ScannerInfo.DecoderType decoderType, Boolean isDefaultScanner,
                                         Boolean isConnected, String modelNumber, int scannerIndex)
    {
        ScannerInfo scannerInfo = null;
        try {
            Constructor<ScannerInfo> scanInfoConstructor = ScannerInfo.class.getDeclaredConstructor(new Class[0]);
            scanInfoConstructor.setAccessible(true);
            scannerInfo = scanInfoConstructor.newInstance(new Object[0]);
            Field fieldFriendlyName = ScannerInfo.class.getDeclaredField(FIELD_FRIENDLY_NAME);
            fieldFriendlyName.setAccessible(true);
            fieldFriendlyName.set(scannerInfo, reportedFriendlyName);
            Field fieldScannerIndex = ScannerInfo.class.getDeclaredField(FIELD_SCANNER_INDEX);
            fieldScannerIndex.setAccessible(true);
            fieldScannerIndex.set(scannerInfo, scannerIndex);

            Field fieldDeviceType = ScannerInfo.class.getDeclaredField(FIELD_DEVICE_TYPE);
            fieldDeviceType.setAccessible(true);
            fieldDeviceType.set(scannerInfo, deviceType);

            Field fieldConnectionType = ScannerInfo.class.getDeclaredField(FIELD_CONNECTION_TYPE);
            fieldConnectionType.setAccessible(true);
            fieldConnectionType.set(scannerInfo, connectionType);

            Field fieldDeviceIdentifier = ScannerInfo.class.getDeclaredField(FIELD_DEVICE_IDENTIFIER);
            fieldDeviceIdentifier.setAccessible(true);
            fieldDeviceIdentifier.set(scannerInfo, deviceIdentifier);

            Field fieldDecoderType = ScannerInfo.class.getDeclaredField(FIELD_DECODER_TYPE);
            fieldDecoderType.setAccessible(true);
            fieldDecoderType.set(scannerInfo, decoderType);

            Field fieldIsDefaultScanner = ScannerInfo.class.getDeclaredField(FIELD_IS_DEFAULT_SCANNER);
            fieldIsDefaultScanner.setAccessible(true);
            fieldIsDefaultScanner.set(scannerInfo, isDefaultScanner);

            Field fieldIsConnected = ScannerInfo.class.getDeclaredField(FIELD_IS_CONNECTED);
            fieldIsConnected.setAccessible(true);
            fieldIsConnected.set(scannerInfo, isConnected);

            Field fieldModelNumber = ScannerInfo.class.getDeclaredField(FIELD_MODEL_NUMBER);
            fieldModelNumber.setAccessible(true);
            fieldModelNumber.set(scannerInfo, modelNumber);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


        return scannerInfo;
    }

    /**
     * Private class used to buffer the scan data
     */
    private class ScanDataStub
    {
        private byte[] scanDataRawBytes;
        private ScanDataCollection.LabelType scanLabelType;
        private String charsetName;
        private String timestamp;
        ScanDataStub(byte[] scanDataRawBytes, ScanDataCollection.LabelType scanLabelType,
                     String charsetName, String timestamp)
        {
            this.scanDataRawBytes = scanDataRawBytes;
            this.scanLabelType = scanLabelType;
            this.charsetName = charsetName;
            this.timestamp = timestamp;
        }
        public byte[] getScanDataRawBytes() {return scanDataRawBytes;}
        public ScanDataCollection.LabelType getScanLabelType() {return scanLabelType;}
        public String getCharsetName() {return charsetName;}
        public String getTimestamp() {return timestamp;}
    }
}
