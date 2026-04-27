package com.app.session;

import java.io.Serializable;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.app.utils.JUtils;
import lombok.Getter;
import lombok.Setter;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Getter
@Setter
public class PageData implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Map<String, Object> data;

    /**
     * Ajout nouvelle categorie avec ses infos
     *
     * @param categorie
     * @param key
     * @param value
     */
    @SuppressWarnings("unchecked")
    public <E> void doStack(Map<String, Object> data, String categorie, String key, E value) {
        if (data == null || !data.containsKey(categorie))
            setData(JUtils.doPut(data, categorie, JUtils.doPut(null, key, value)));
        else
            JUtils.doPut((Map<String, E>) data.get(categorie), key, value);
    }

    @SuppressWarnings("unchecked")
    public <E> E get(String categorie, String key) {
        if (getData() != null && getData().containsKey(categorie))
            return (E) ((Map<String, Object>) getData().get(categorie)).get(key);
        return null;
    }

    @SuppressWarnings("unchecked")
    public void remove(String categorie, String key) {
        if (getData() != null && getData().containsKey(categorie))
            ((Map<String, Object>) getData().get(categorie)).remove(key);

    }
}

