package simpledb;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Tuple maintains information about the contents of a tuple. Tuples have a
 * specified schema specified by a TupleDesc object and contain Field objects
 * with the data for each field.
 */
public class Tuple implements Serializable {

    private static final long serialVersionUID = 1L;

    Field[] fields;
    TupleDesc td;
    RecordId rid;

    /**
     * Create a new tuple with the specified schema (type).
     * 
     * @param td
     *            the schema of this tuple. It must be a valid TupleDesc
     *            instance with at least one field.
     */
    public Tuple(TupleDesc td) {
        // some code goes here
        if (td.numFields() < 1){
            return ;
        }

        this.fields = new Field[td.numFields()];
        this.td = td;

        for(int i = 0; i < td.numFields(); i++){
            if(td.getFieldType(i) == Type.INT_TYPE){
                this.fields[i] = new IntField(0);
            }
            else if(td.getFieldType(i) == Type.STRING_TYPE){
                this.fields[i] = new StringField("", 0);
            }
            else{
                throw new NoSuchElementException();
            }
        }
    }

    /**
     * @return The TupleDesc representing the schema of this tuple.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        return this.td;
    }

    /**
     * @return The RecordId representing the location of this tuple on disk. May
     *         be null.
     */
    public RecordId getRecordId() {
        // some code goes here
        return this.rid;
    }

    /**
     * Set the RecordId information for this tuple.
     * 
     * @param rid
     *            the new RecordId for this tuple.
     */
    public void setRecordId(RecordId rid) {
        // some code goes here
        this.rid = rid;
    }

    /**
     * Change the value of the ith field of this tuple.
     * 
     * @param i
     *            index of the field to change. It must be a valid index.
     * @param f
     *            new value for the field.
     */
    public void setField(int i, Field f) {
        // some code goes here
        if (i < 0 || i >= td.numFields()){
            return ;
        }

        this.fields[i] = f;
    }

    /**
     * @return the value of the ith field, or null if it has not been set.
     * 
     * @param i
     *            field index to return. Must be a valid index.
     */
    public Field getField(int i) {
        // some code goes here
        if (i >= td.numFields()){
            // error
            return null;
        }

        return this.fields[i];
    }

    /**
     * Returns the contents of this Tuple as a string. Note that to pass the
     * system tests, the format needs to be as follows:
     * 
     * column1\tcolumn2\tcolumn3\t...\tcolumnN\n
     * 
     * where \t is any whitespace, except newline, and \n is a newline
     */
    public String toString() {
        // some code goes here
        //throw new UnsupportedOperationException("Implement this");
        String str = "";
        for(int i = 0; i < this.fields.length; i++){
            str += this.fields[i].toString() + "\t";
        }
        str += "\n";

        return str;
    }

    /*
    * class Iter Added by EOF
    * */

    private class Iter implements Iterator<Field>{
        int cursor  = 0;
        int lastRet = -1;

        public boolean hasNext(){
            return cursor != td.numFields();
        }

        public Field next(){
            if(! hasNext()){
                throw new NoSuchElementException();
            }

            int i = cursor;
            Object[] obj = fields;
            cursor += 1;
            lastRet = i;
            return (Field) obj[lastRet];
        }
    }
    
    /**
     * @return
     *        An iterator which iterates over all the fields of this tuple
     * */
    public Iterator<Field> fields()
    {
        // some code goes here
        return new Iter();
    }
    
    /**
     * reset the TupleDesc of thi tuple
     * */
    public void resetTupleDesc(TupleDesc td)
    {
        // some code goes here
        this.td = td;
    }
}
