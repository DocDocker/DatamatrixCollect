package com.example.datamatrixcollecting;

public class MarkingCode {

    private String FullCode;
    private String GTIN;
    private String Serial;
    private String Part91;
    private String Part92;
    private String UniqueCode;

    public String getFullCode() {
        return FullCode;
    }

    public String getGTIN() {
        return GTIN;
    }

    public String getSerial() {
        return Serial;
    }

    public String getPart91() {
        return Part91;
    }

    public String getPart92() {
        return Part92;
    }

    public String getUniqueCode() {
        return UniqueCode;
    }

    public MarkingCode(String RawBarcodeData) throws Exception {

        if (IsCorrectMarkingCode(RawBarcodeData)) {
            this.FullCode = RawBarcodeData;
            this.GTIN = RawBarcodeData.substring(2, 16);
            this.Serial = RawBarcodeData.substring(18, 31);
            this.Part91 = RawBarcodeData.substring(34, 38);
            this.Part92 = RawBarcodeData.substring(41);
            this.UniqueCode = "(01)" + this.GTIN + "(21)" + this.Serial;
        } else {
            throw new Exception();
        }
    }

    private boolean IsCorrectMarkingCode(String RawBarcodeData){
        if (RawBarcodeData.substring(0, 2).equals("01")
                && RawBarcodeData.substring(16, 18).equals("21")
                && RawBarcodeData.substring(31, 32).equals(Character.toString((char)29))
                && RawBarcodeData.substring(38, 39).equals(Character.toString((char)29))) {
            return true;
        } else {
            return false;
        }
    }

}
