package com.mfashiongallery.emag.preview.model;

/**
 * Created by liuchonghui on 16/6/2.
 */
public enum PicEnum {

    PIC_0("http://wallpaper.cdn.pandora.xiaomi.com/thumbnail/webp/w720/MiTv/08dca4796ddb26cbfb1d7d27a7850879fd841db78"),
    PIC_1("http://wallpaper.cdn.pandora.xiaomi.com/thumbnail/webp/w720/MiTv/0bb8654a9b11447f6341df83518721e1836160df6"),
    PIC_2("http://wallpaper.cdn.pandora.xiaomi.com/thumbnail/webp/w720/MiTv/0d487485bfe9dbaf437e6bb8afab3adece14266ee"),
    PIC_3("http://wallpaper.cdn.pandora.xiaomi.com/thumbnail/webp/w720/MiTv/0523a4bcfd3b184196223821c3b4a1981ec43ec7d"),
    PIC_4("http://wallpaper.cdn.pandora.xiaomi.com/thumbnail/webp/w720/MiTv/04bb25ed378722f947a595945bd283ecc6e414f9f"),
    PIC_5("http://wallpaper.cdn.pandora.xiaomi.com/thumbnail/webp/w720/MiTv/0e4cf4d1de916fcd19b2e9d520ba74ae55a41581d"),
    PIC_6("http://wallpaper.cdn.pandora.xiaomi.com/thumbnail/webp/w720/MiTv/0925c24e16b6d42c8208eff1d70b61a331aa56005"),
    PIC_7("http://wallpaper.cdn.pandora.xiaomi.com/thumbnail/webp/w720/MiTv/02121f57ca1b548863c33bba511c19bbfd69cbe4b"),
    PIC_8("http://wallpaper.cdn.pandora.xiaomi.com/thumbnail/webp/w720/MiTv/06aec5126e269fb4ed9cbcacf4741ef1ef743ab5a"),
    PIC_9("http://wallpaper.cdn.pandora.xiaomi.com/thumbnail/webp/w720/MiTv/0aa6e5d462f6758a882e32839b1a792f23940520e");

    PicEnum(String url) {
        this.url = url;
    }

    public final String url;

    public String getUrl() {
        return this.url;
    }

    public static String get(int position) {
        int size = values().length;
        return values()[position % size].getUrl();
    }
}
