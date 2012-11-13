
package com.ntu.whereRU.data.memprovider;


public class MemoryProvider {

    private static MemoryProvider sInstance;

    public static MemoryProvider getInstance() {
        if (sInstance == null) {
            sInstance = new MemoryProvider();
        }
        return sInstance;
    }

    public static void resetInstance() {
        sInstance = null;
    }

    private MemoryProvider() {

    }

    // CityList WS
//    public ArrayList<City> cityList;
//
//    // SyncPhoneList WS
//    public ArrayList<Phone> syncPhoneList;
//
//    // SyncPhoneDelete WS
//    public long[] syncPhoneDeleteData;
//
//    // SyncPhoneAdd WS
//    public Phone syncPhoneAddedPhone;
//
//    // SyncPhoneEdit WS
//    public Phone syncPhoneEditedPhone;
//
//    // RssFeed WS
//    public RssFeed rssFeed;
}
