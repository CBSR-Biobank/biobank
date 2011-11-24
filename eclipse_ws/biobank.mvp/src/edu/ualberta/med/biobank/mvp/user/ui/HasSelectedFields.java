package edu.ualberta.med.biobank.mvp.user.ui;

import java.util.Collection;

public interface HasSelectedFields<T> extends HasField<Collection<T>>,
    HasOptions<T> {
}
