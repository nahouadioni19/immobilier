package com.app.entities.rapport;

import com.app.entities.BaseEntity;
import com.app.utils.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class POJOEtat extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String infoJrxml;

    private String params;

    private String format;

    public POJOEtat(String inforJrxml, String params, String format) {
        this.infoJrxml = inforJrxml;
        this.params = params;
        this.format = format;
    }

    public boolean isExcel() {
        return Constants.EXCEL.equals(this.format);
    }
    public boolean isPdf() {
        return Constants.PDF.equals(this.format) || this.format == null;
    }
}

