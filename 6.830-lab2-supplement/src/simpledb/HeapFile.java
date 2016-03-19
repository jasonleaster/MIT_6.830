package simpledb;


import java.io.*;
import java.util.*;

/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples
 * in no particular order. Tuples are stored on pages, each of which is a fixed
 * size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage
 * constructor.
 * 
 * @see HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile {

    private final File f;
    private TupleDesc td;
    private int pageNum;
    int curPageNum;
    private int offset;
    /**
     * Constructs a heap file backed by the specified file.
     * 
     * @param f
     *            the file that stores the on-disk backing store for this heap
     *            file.
     */
    public HeapFile(File f, TupleDesc td) {
        // some code goes here
        this.f          = f;
        this.td         = td;
        this.pageNum    = (int)this.f.length() / BufferPool.getPageSize();
        this.offset     = 0;
        this.curPageNum = 0;
    }

    /**
     * Returns the File backing this HeapFile on disk.
     * 
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        // some code goes here
        return this.f;
    }

    /**
     * Returns an ID uniquely identifying this HeapFile. Implementation note:
     * you will need to generate this tableid somewhere ensure that each
     * HeapFile has a "unique id," and that you always return the same value for
     * a particular HeapFile. We suggest hashing the absolute file name of the
     * file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
     * 
     * @return an ID uniquely identifying this HeapFile.
     */
    public int getId() {
        // some code goes here
        //throw new UnsupportedOperationException("implement this");
        return f.getAbsoluteFile().hashCode();
    }

    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     * 
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        //throw new UnsupportedOperationException("implement this");
        return this.td;
    }

    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        // some code goes here
        HeapPageId hpid  = (HeapPageId)pid;
        HeapPage hp = null;

        try {
            InputStream is  = new FileInputStream(this.f);
            byte[] buf      = new byte[BufferPool.getPageSize()];
            int count       = 0;
            int available   = 0;
            try {
                is.skip(this.offset);

                available = is.available();
                if(available > 0) {
                    try {
                        count = is.read(buf, 0, Math.min(BufferPool.getPageSize(), available));
                    }catch (IndexOutOfBoundsException e){
                        System.out.println(available);
                    }
                    this.offset += count;

                    hp = new HeapPage(hpid, buf);
                }
                // Close the input stream and return bytes
                is.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return hp;
    }

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        // some code goes here
        // not necessary for lab1
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        // some code goes here
        return this.pageNum;
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for lab1
    }

    // see DbFile.java for javadocs
    public Page deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for lab1
    }

    private class Iter implements DbFileIterator {

        TransactionId tid                 = null;
        boolean opened                    = false;
        Iterator<Tuple> iterOfCurrentPage = null;
        int currentPageNum = 0;

        public Iter(TransactionId tid){
            this.tid = tid;
        }

        @Override
        public void rewind() throws DbException, TransactionAbortedException {
            this.close();
            this.open();
        }

        @Override
        public boolean hasNext() throws DbException, TransactionAbortedException {
            if(this.opened == false) {
                return false;
            }

            if(this.iterOfCurrentPage == null ||
                    (this.iterOfCurrentPage != null && this.iterOfCurrentPage.hasNext() == false))
            {
                if(currentPageNum == HeapFile.this.pageNum){
                    return false;
                }

                HeapPageId hpid = new HeapPageId(HeapFile.this.getId(), this.currentPageNum);
                HeapPage p = (HeapPage) Database.getBufferPool().getPage(tid, hpid, Permissions.READ_WRITE);
                if(p == null){
                    return false;
                }else{
                    currentPageNum += 1;
                }
                this.iterOfCurrentPage = p.iterator();
                if(this.iterOfCurrentPage.hasNext()){
                    return true;
                }else{
                    return false;
                }
            }

            return true;
        }

        @Override
        public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException {
            if(! hasNext()){
                throw new NoSuchElementException();
            }

            return this.iterOfCurrentPage.next();
        }

        @Override
        public void open() throws DbException, TransactionAbortedException {
            this.opened = true;
        }

        @Override
        public void close() {
            this.opened = false;
        }
    }
    // see DbFile.java for javadocs
    public DbFileIterator iterator(TransactionId tid) {
        // some code goes here
        return new Iter(tid);
    }

}

