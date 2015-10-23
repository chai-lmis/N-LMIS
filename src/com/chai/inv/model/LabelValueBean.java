package com.chai.inv.model;

import java.util.Objects;

public class LabelValueBean {
    private String label;
    private String value;
    private String extra;
    private String extra1;
    private String extra2;
    private String extra3;
    private String extra4;

    public LabelValueBean() {
    }

    public LabelValueBean(String label, String value) {
        this.label = label;
        this.value = value;
    }
    
    public LabelValueBean(String label, String value, String extra) {
        this.label = label;
        this.value = value;
        this.extra = extra;
    }
    public LabelValueBean(String label, String value, String extra, String extra1) {
        this.label = label;
        this.value = value;
        this.extra = extra;
        this.extra1 = extra1;
    }
    public LabelValueBean(String label, String value, String extra, String extra1, String extra2) {
        this.label = label;
        this.value = value;
        this.extra = extra;
        this.extra1 = extra1;
        this.extra2 = extra2;
    }
    public LabelValueBean(String label, String value, String extra, String extra1, String extra2, String extra3) {
        this.label = label;
        this.value = value;
        this.extra = extra;
        this.extra1 = extra1;
        this.extra2 = extra2;
        this.extra3 = extra3;
    }
    public LabelValueBean(String label, String value, String extra, String extra1, String extra2, String extra3, String extra4) {
        this.label = label;
        this.value = value;
        this.extra = extra;
        this.extra1 = extra1;
        this.extra2 = extra2;
        this.extra3 = extra3;
        this.extra4 = extra4;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getExtra1() {
        return extra1;
    }

    public void setExtra1(String extra1) {
        this.extra1 = extra1;
    }
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getExtra2() {
        return extra2;
    }

    public void setExtra2(String extra2) {
        this.extra2 = extra2;
    }

    public String getExtra3() {
        return extra3;
    }

    public void setExtra3(String extra3) {
        this.extra3 = extra3;
    }

    public String getExtra4() {
        return extra4;
    }

    public void setExtra4(String extra4) {
        this.extra4 = extra4;
    }

    @Override
    public String toString(){
        return label;
    }

    @Override
       public boolean equals(Object anObject) {
        if(anObject == null)
            return false;
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof LabelValueBean) {
            LabelValueBean lvb = (LabelValueBean)anObject;
            if(this.value !=null && this.value.equals(lvb.value))
                return true;
            else
                return false;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.value);
        return hash;
    }
}