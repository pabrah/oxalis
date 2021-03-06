package eu.peppol.persistence.api;

import java.io.Serializable;

/**
 * User: andy
 * Date: 10/5/12
 * Time: 1:32 PM
 */
public class MessageNumber implements Serializable, Comparable<MessageNumber> {

    private static final long serialVersionUID = 2009L;

    private final Long messageNo;

    private MessageNumber(Long messageNo) {
        if (messageNo == null)
            throw new IllegalArgumentException("Message number can not be null");
        this.messageNo = messageNo;
    }

    public static MessageNumber create(Long messageNo){
        if (messageNo == null) {
            throw new IllegalArgumentException("Message number cannot be null");
        } else if (messageNo < 1) {
            throw new IllegalArgumentException("Message number cannot be < 0");
        }
        return new MessageNumber(messageNo);
    }


    /**
     *
     * @deprecated use the Long constructor
     */
    @Deprecated
    public static MessageNumber create(Integer messageNo){
        if (messageNo == null) {
            throw new IllegalArgumentException("Message number cannot be null");
        } else if (messageNo < 1) {
            throw new IllegalArgumentException("Message number cannot be < 0");
        }
        return new MessageNumber(new Long(messageNo));
    }


    public static MessageNumber valueOf(String s) {
        try {
            Long longValue = Long.valueOf(s);
            return create(longValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("%s is not a valid message number.", s));

        }
    }

    public String getValue() {
        return messageNo == null ? "" : messageNo.toString();
    }
    public Integer toInt(){
        return messageNo.intValue();
    }
    public Long toLong() { return messageNo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageNumber messageNo1 = (MessageNumber) o;

        if (messageNo != null ? !messageNo.equals(messageNo1.messageNo) : messageNo1.messageNo != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return messageNo != null ? messageNo.hashCode() : 0;
    }

    @Override
    public String toString() {
        return messageNo.toString();
    }

    public int compareTo(MessageNumber o) {
        if(o == null){
            return -1;
        }
        return this.messageNo.compareTo(o.messageNo);
    }

}
