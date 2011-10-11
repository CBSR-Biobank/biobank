package edu.ualberta.med.biobank.mvp.user.ui;

import java.util.List;

import edu.ualberta.med.biobank.mvp.util.Converter;

public interface HasOptions<T> {
    void setOptions(List<T> options);

    void setOptionLabeler(Converter<T, String> labeler);
}
