/*
 * This file is generated by jOOQ.
 */
package no.nav.veilarblest.domain;


import jakarta.annotation.Generated;

import org.jooq.Sequence;
import org.jooq.impl.SequenceImpl;


/**
 * Convenience access to all sequences in public
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.7"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Sequences {

    /**
     * The sequence <code>public.lest_id_seq</code>
     */
    public static final Sequence<Integer> LEST_ID_SEQ = new SequenceImpl<Integer>("lest_id_seq", Public.PUBLIC, org.jooq.impl.SQLDataType.INTEGER.nullable(false));

    /**
     * The sequence <code>public.mine_ressurser_id_seq</code>
     */
    public static final Sequence<Integer> MINE_RESSURSER_ID_SEQ = new SequenceImpl<Integer>("mine_ressurser_id_seq", Public.PUBLIC, org.jooq.impl.SQLDataType.INTEGER.nullable(false));
}
