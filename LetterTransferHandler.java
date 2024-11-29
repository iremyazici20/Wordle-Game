package dragDrop;

import enums.LetterType;

import javax.swing.*;
import java.awt.datatransfer.*;
import java.io.IOException;

/*
 * Kaynak:
 * http://www.iitk.ac.in/esc101/05Aug/tutorial/uiswing/misc/example-1dot4/index.html#DragPictureDemo
 */

// Harf hucreleri arasindaki veri transferini saglayan transfer isleyici sinifi
public class LetterTransferHandler extends TransferHandler {

    // DataFlavor sinifinda charFlavor olmadigi icin stringFlavor kullanildi
    // Bu islem runtimeda surukle-birak isleminden sonra hata verse de uygulama dogru bir sekilde calisiyor
    private final DataFlavor charFlavor = DataFlavor.stringFlavor;
    // Suruklenen harfteki veri kopyalanacagi icin kaynak ayrica tutuluyor
    private DTLetter sourceLetter;

    @Override
    public boolean importData(JComponent comp, Transferable t) {
        Character c;
        // Veri aktarimi yapilabiliyor mu diye kontrol ediliyor
        if (canImport(comp, t.getTransferDataFlavors())) {
            // Eger aktarim mumkunse aktarimin yapilacagi harfin nesnesi olusturuluyor
            DTLetter letter = (DTLetter) comp;
            if (sourceLetter == letter) {
                // Kaynak ve hedef ayni ise hicbir islem yapilmadan true donuyor
                return true;
            } else if (letter.letterType == LetterType.GUESS_LETTER) {
                // Kaynak, hedeften farkli ve hedef bir tahmin harfiyse transfer gerceklestiriliyor
                // Harf tipinin kontrol edilme sebebi klavyedeki harflerin birbiri uzerine surukle-birak yapilmasinin onune gecmek
                try {
                    c = (Character) t.getTransferData(charFlavor);
                    letter.setChar(c);
                    return true;
                } catch (UnsupportedFlavorException ufe) {
                    System.out.println("importData: unsupported data flavor");
                } catch (IOException ioe) {
                    System.out.println("importData: I/O exception");
                }
            }
        }
        return false;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        DTLetter letter = (DTLetter) c;
        // Harf tipinin klavye olup olmadigi kontrol ediliyor
        if (letter.letterType == LetterType.KEYBOARD_LETTER) {
            // Eger klavye harfi ise kaynak harf olarak ataniyor
            // Buradaki kontrolun sebebi tahmin harflerinin surukle-birak ozelligine sahip olmamasini saglamak
            sourceLetter = (DTLetter) c;
            return new LetterTransferable(sourceLetter);
        } else return null;
    }

    // Aksiyon olarak kopyalama seciliyor
    // Tasima secilseydi klavyeden tahmine suruklendiginde klavyedeki harf silinmis olacakti
    public int getSourceActions(JComponent c) {
        return COPY;
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {}

    // Aktarim isleminin yapilip yapilamayacagini kontrol eden metot
    @Override
    public boolean canImport(JComponent comp, DataFlavor[] flavors) {
        for (DataFlavor flavor : flavors) {
            // Flavorlar uyusuyorsa aktarim yapilabilir
            if (charFlavor.equals(flavor)) {
                return true;
            }
        }
        return false;
    }

    // Transfer edilebilir harf inner-class olarak olusturuluyor
    class LetterTransferable implements Transferable {

        private Character c;

        // Harf atamasi yapiliyor
        LetterTransferable(DTLetter letter) {
            this.c = letter.c;
        }

        // Flavor listesini gonderen metot
        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] { charFlavor };
        }

        // Flavor destegi olup olmadigini kontrol eden metot
        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return charFlavor.equals(flavor);
        }

        // Transfer edilen veriyi donduren metot
        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return c;
        }
    }
}
