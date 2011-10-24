package edu.ualberta.med.biobank.mvp.user.ui;

import java.util.List;

import edu.ualberta.med.biobank.mvp.util.Converter;

public interface HasOptions<T> {
    void setOptions(List<T> options);

    void setOptionLabeller(Converter<T, String> labeler);
}
