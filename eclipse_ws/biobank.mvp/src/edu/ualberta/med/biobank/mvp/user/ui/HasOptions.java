package edu.ualberta.med.biobank.mvp.user.ui;

import java.util.List;

import edu.ualberta.med.biobank.mvp.util.Converter;

public interface HasOptions<E> {
    void setOptions(List<E> options);

    void setOptionLabeller(Converter<E, String> labeler);
}
