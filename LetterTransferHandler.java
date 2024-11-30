package dragDrop;

import enums.LetterType;

import javax.swing.*;
import java.awt.datatransfer.*;
import java.io.IOException;

/*
 * http://www.iitk.ac.in/esc101/05Aug/tutorial/uiswing/misc/example-1dot4/index.html#DragPictureDemo
 */


public class LetterTransferHandler extends TransferHandler {


    private final DataFlavor charFlavor = DataFlavor.stringFlavor;
    private DTLetter sourceLetter;

    @Override
    public boolean importData(JComponent comp, Transferable t) {
        Character c;

        if (canImport(comp, t.getTransferDataFlavors())) {
        
            DTLetter letter = (DTLetter) comp;
            if (sourceLetter == letter) {
                return true;
            } else if (letter.letterType == LetterType.GUESS_LETTER) {
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
        if (letter.letterType == LetterType.KEYBOARD_LETTER) {
            sourceLetter = (DTLetter) c;
            return new LetterTransferable(sourceLetter);
        } else return null;
    }
    public int getSourceActions(JComponent c) {
        return COPY;
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {}


    @Override
    public boolean canImport(JComponent comp, DataFlavor[] flavors) {
        for (DataFlavor flavor : flavors) {
            if (charFlavor.equals(flavor)) {
                return true;
            }
        }
        return false;
    }

    class LetterTransferable implements Transferable {

        private Character c;
        LetterTransferable(DTLetter letter) {
            this.c = letter.c;
        }
        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] { charFlavor };
        }
        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return charFlavor.equals(flavor);
        }
        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return c;
        }
    }
}
