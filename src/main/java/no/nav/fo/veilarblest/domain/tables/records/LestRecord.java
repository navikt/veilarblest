/*
 * This file is generated by jOOQ.
 */
package no.nav.fo.veilarblest.domain.tables.records;


import java.time.LocalDateTime;

import javax.annotation.Generated;

import no.nav.fo.veilarblest.domain.enums.Ressurs;
import no.nav.fo.veilarblest.domain.tables.Lest;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.7"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LestRecord extends UpdatableRecordImpl<LestRecord> implements Record5<Integer, String, String, Ressurs, LocalDateTime> {

    private static final long serialVersionUID = 25142694;

    /**
     * Setter for <code>public.lest.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.lest.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.lest.eier</code>.
     */
    public void setEier(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.lest.eier</code>.
     */
    public String getEier() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.lest.av</code>.
     */
    public void setAv(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.lest.av</code>.
     */
    public String getAv() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.lest.ressurs</code>.
     */
    public void setRessurs(Ressurs value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.lest.ressurs</code>.
     */
    public Ressurs getRessurs() {
        return (Ressurs) get(3);
    }

    /**
     * Setter for <code>public.lest.tidspunkt</code>.
     */
    public void setTidspunkt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.lest.tidspunkt</code>.
     */
    public LocalDateTime getTidspunkt() {
        return (LocalDateTime) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<Integer, String, String, Ressurs, LocalDateTime> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<Integer, String, String, Ressurs, LocalDateTime> valuesRow() {
        return (Row5) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Lest.LEST.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Lest.LEST.EIER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Lest.LEST.AV;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Ressurs> field4() {
        return Lest.LEST.RESSURS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field5() {
        return Lest.LEST.TIDSPUNKT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getEier();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getAv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Ressurs component4() {
        return getRessurs();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime component5() {
        return getTidspunkt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getEier();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getAv();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Ressurs value4() {
        return getRessurs();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value5() {
        return getTidspunkt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LestRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LestRecord value2(String value) {
        setEier(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LestRecord value3(String value) {
        setAv(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LestRecord value4(Ressurs value) {
        setRessurs(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LestRecord value5(LocalDateTime value) {
        setTidspunkt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LestRecord values(Integer value1, String value2, String value3, Ressurs value4, LocalDateTime value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached LestRecord
     */
    public LestRecord() {
        super(Lest.LEST);
    }

    /**
     * Create a detached, initialised LestRecord
     */
    public LestRecord(Integer id, String eier, String av, Ressurs ressurs, LocalDateTime tidspunkt) {
        super(Lest.LEST);

        set(0, id);
        set(1, eier);
        set(2, av);
        set(3, ressurs);
        set(4, tidspunkt);
    }
}
