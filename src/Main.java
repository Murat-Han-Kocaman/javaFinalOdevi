import java.util.*;
import java.util.Scanner;

abstract class Hesap {
    private static int nextID = 1;
    private int ID;
    private int bakiye;

    public Hesap(int baslangicBakiyesi) {
        this.ID = nextID++;
        this.bakiye = baslangicBakiyesi;
    }

    public int getID() {
        return ID;
    }

    public int getBakiye() {
        return bakiye;
    }

    public void paraYatir(int miktar) {
        bakiye += miktar;
        System.out.println("Hesap No: " + ID + " - " + miktar + " TL yatırıldı. Yeni bakiye: " + bakiye + " TL");
    }

    public void paraCek(int miktar) {
        if (miktar <= bakiye) {
            bakiye -= miktar;
            System.out.println("Hesap No: " + ID + " - " + miktar + " TL çekildi. Yeni bakiye: " + bakiye + " TL");
        } else {
            System.out.println("Hesap No: " + ID + " - Yetersiz bakiye. İşlem gerçekleştirilemedi.");
        }
    }

    public abstract double karHesapla();
}

class KisaVadeliHesap extends Hesap {
    private static final double FAIZ_ORANI = 0.17;

    public KisaVadeliHesap(int baslangicBakiyesi) {
        super(baslangicBakiyesi);
    }

    @Override
    public double karHesapla() {
        return getBakiye() * FAIZ_ORANI;
    }
}

class UzunVadeliHesap extends Hesap {
    private static final double FAIZ_ORANI = 0.24;
    private Date acilisTarihi;

    public UzunVadeliHesap(int baslangicBakiyesi) {
        super(baslangicBakiyesi);
        acilisTarihi = new Date();
    }

    public Date getAcilisTarihi() {
        return acilisTarihi;
    }

    @Override
    public double karHesapla() {
        Date bugun = new Date();
        long gunSayisi = (bugun.getTime() - acilisTarihi.getTime()) / (1000 * 60 * 60 * 24);
        double yillikKar = getBakiye() * FAIZ_ORANI;
        double gunlukKar = yillikKar / 365;
        return gunlukKar * gunSayisi;
    }
}

class OzelHesap extends Hesap {
    private static final double FAIZ_ORANI = 0.12;
    private int puan;

    public OzelHesap(int baslangicBakiyesi) {
        super(baslangicBakiyesi);
        this.puan = baslangicBakiyesi / 2000;
    }

    public int getPuan() {
        return puan;
    }

    @Override
    public double karHesapla() {
        return getBakiye() * FAIZ_ORANI;
    }
}

class CariHesap extends Hesap {
    public CariHesap() {
        super(0);
    }

    @Override
    public double karHesapla() {
        return 0;
    }
}

class Banka {
    private List<Hesap> hesaplar;
    private Date sistemTarihi;

    public Banka() {
        hesaplar = new ArrayList<>();
    }

    public void sistemTarihiniAyarla(int yil, int ay, int gun) {
        Calendar takvim = Calendar.getInstance();
        takvim.set(yil, ay - 1, gun);
        sistemTarihi = takvim.getTime();
    }

    public void kisaVadeliHesapAc(int baslangicBakiyesi) {
        KisaVadeliHesap hesap = new KisaVadeliHesap(baslangicBakiyesi);
        hesaplar.add(hesap);
        System.out.println("Yeni kısa vadeli hesap oluşturuldu. Hesap No: " + hesap.getID());
    }

    public void uzunVadeliHesapAc(int baslangicBakiyesi) {
        UzunVadeliHesap hesap = new UzunVadeliHesap(baslangicBakiyesi);
        hesaplar.add(hesap);
        System.out.println("Yeni uzun vadeli hesap oluşturuldu. Hesap No: " + hesap.getID());
    }

    public void ozelHesapAc(int baslangicBakiyesi) {
        OzelHesap hesap = new OzelHesap(baslangicBakiyesi);
        hesaplar.add(hesap);
        System.out.println("Yeni özel hesap oluşturuldu. Hesap No: " + hesap.getID());
    }

    public void cariHesapAc() {
        CariHesap hesap = new CariHesap();
        hesaplar.add(hesap);
        System.out.println("Yeni cari hesap oluşturuldu. Hesap No: " + hesap.getID());
    }

    public void paraYatir(int hesapNo, int miktar) {
        Hesap hesap = hesapIDBul(hesapNo);
        if (hesap != null) {
            hesap.paraYatir(miktar);
        } else {
            System.out.println("Hesap bulunamadı.");
        }
    }

    public void paraCek(int hesapNo, int miktar) {
        Hesap hesap = hesapIDBul(hesapNo);
        if (hesap != null) {
            hesap.paraCek(miktar);
        } else {
            System.out.println("Hesap bulunamadı.");
        }
    }

    public void kuraCek() {
        List<OzelHesap> ozelHesaplar = new ArrayList<>();
        for (Hesap hesap : hesaplar) {
            if (hesap instanceof OzelHesap) {
                ozelHesaplar.add((OzelHesap) hesap);
            }
        }

        if (ozelHesaplar.isEmpty()) {
            System.out.println("Kurada çekilecek özel hesap bulunamadı.");
        } else {
            int toplamPuan = 0;
            for (OzelHesap hesap : ozelHesaplar) {
                toplamPuan += hesap.getPuan();
            }

            Random random = new Random();
            int cekilisPuan = random.nextInt(toplamPuan) + 1;
            int toplam = 0;
            OzelHesap secilenHesap = null;
            for (OzelHesap hesap : ozelHesaplar) {
                toplam += hesap.getPuan();
                if (cekilisPuan <= toplam) {
                    secilenHesap = hesap;
                    break;
                }
            }

            if (secilenHesap != null) {
                secilenHesap.paraYatir(10000);
                System.out.println("Kurada çekilen hesap: " + secilenHesap.getID());
                System.out.println("Çekilen hesaba 10.000 TL ödül yatırıldı.");
            } else {
                System.out.println("Kurada çekilen hesap bulunamadı.");
            }
        }
    }

    public void hesaplarListele() {
        for (Hesap hesap : hesaplar) {
            System.out.println("Hesap No: " + hesap.getID() + " - Son 5 işlem: ");
            System.out.println("  Bakiye: " + hesap.getBakiye() + " TL");
            System.out.println("  Kar: " + hesap.karHesapla() + " TL");
            System.out.println();
        }
    }

    public void hesapNumaralariniListele() {
        for (Hesap hesap : hesaplar) {
            System.out.println("Hesap No: " + hesap.getID());
        }
    }

    private Hesap hesapIDBul(int hesapNo) {
        for (Hesap hesap : hesaplar) {
            if (hesap.getID() == hesapNo) {
                return hesap;
            }
        }
        return null;
    }
}


public class Main {
    public static void main(String[] args) {
        Banka banka = new Banka();
        banka.sistemTarihiniAyarla(2023, 5, 5);

        Scanner scanner = new Scanner(System.in);
        int secim = 0;
        do {
            System.out.println("********** Banka Uygulaması **********");
            System.out.println("1. Kısa Vadeli Hesap Aç");
            System.out.println("2. Uzun Vadeli Hesap Aç");
            System.out.println("3. Özel Hesap Aç");
            System.out.println("4. Cari Hesap Aç");
            System.out.println("5. Para Yatır");
            System.out.println("6. Para Çek");
            System.out.println("7. Kura Çek");
            System.out.println("8. Hesapları Listele");
            System.out.println("9. Hesap Numaralarını Listele");
            System.out.println("10. Tarihi Değiştir");
            System.out.println("0. Çıkış");
            System.out.println("***************************************");
            System.out.print("Seçiminizi yapın: ");
            secim = scanner.nextInt();

            switch (secim) {
                case 1:
                    System.out.print("Başlangıç bakiyesini girin: ");
                    int kisaVadeliBakiye = scanner.nextInt();
                    banka.kisaVadeliHesapAc(kisaVadeliBakiye);
                    break;
                case 2:
                    System.out.print("Başlangıç bakiyesini girin: ");
                    int uzunVadeliBakiye = scanner.nextInt();
                    banka.uzunVadeliHesapAc(uzunVadeliBakiye);
                    break;
                case 3:
                    System.out.print("Başlangıç bakiyesini girin: ");
                    int ozelHesapBakiye = scanner.nextInt();
                    banka.ozelHesapAc(ozelHesapBakiye);
                    break;
                case 4:
                    banka.cariHesapAc();
                    break;
                case 5:
                    System.out.print("Hesap numarasını girin: ");
                    int yatirHesapNo = scanner.nextInt();
                    System.out.print("Yatırılacak miktarı girin: ");
                    int yatirMiktar = scanner.nextInt();
                    banka.paraYatir(yatirHesapNo, yatirMiktar);
                    break;
                case 6:
                    System.out.print("Hesap numarasını girin: ");
                    int cekHesapNo = scanner.nextInt();
                    System.out.print("Çekilecek miktarı girin: ");
                    int cekMiktar = scanner.nextInt();
                    banka.paraCek(cekHesapNo, cekMiktar);
                    break;
                case 7:
                    banka.kuraCek();
                    break;
                case 8:
                    banka.hesaplarListele();
                    break;
                case 9:
                    banka.hesapNumaralariniListele();
                    break;
                case 10:
                    System.out.print("Yeni tarihi girin (dd mm yyyy): ");
                    int gun = scanner.nextInt();
                    int ay = scanner.nextInt();
                    int yil = scanner.nextInt();
                    banka.sistemTarihiniAyarla(yil, ay, gun);
                    break;
                case 0:
                    System.out.println("Çıkış yapılıyor...");
                    break;
                default:
                    System.out.println("Geçersiz seçim. Tekrar deneyin.");
            }

            System.out.println();
        } while (secim != 0);

        scanner.close();
    }
}



