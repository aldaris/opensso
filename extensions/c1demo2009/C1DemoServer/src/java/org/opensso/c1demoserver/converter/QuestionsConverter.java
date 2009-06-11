/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensso.c1demoserver.converter;

import java.net.URI;
import java.util.Collection;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlAttribute;
import java.util.ArrayList;
import org.opensso.c1demoserver.model.Question;

/**
 *
 * @author pat
 */

@XmlRootElement(name = "questions")
public class QuestionsConverter {
    private Collection<Question> entities;
    private Collection<QuestionConverter> items;
    private URI uri;
    private int expandLevel;
  
    /** Creates a new instance of QuestionsConverter */
    public QuestionsConverter() {
    }

    /**
     * Creates a new instance of QuestionsConverter.
     *
     * @param entities associated entities
     * @param uri associated uri
     * @param expandLevel indicates the number of levels the entity graph should be expanded
     */
    public QuestionsConverter(Collection<Question> entities, URI uri, int expandLevel) {
        this.entities = entities;
        this.uri = uri;
        this.expandLevel = expandLevel;
        getQuestion();
    }

    /**
     * Returns a collection of QuestionConverter.
     *
     * @return a collection of QuestionConverter
     */
    @XmlElement
    public Collection<QuestionConverter> getQuestion() {
        if (items == null) {
            items = new ArrayList<QuestionConverter>();
        }
        if (entities != null) {
            items.clear();
            for (Question entity : entities) {
                items.add(new QuestionConverter(entity, uri, expandLevel, true));
            }
        }
        return items;
    }

    /**
     * Sets a collection of QuestionConverter.
     *
     * @param a collection of QuestionConverter to set
     */
    public void setQuestion(Collection<QuestionConverter> items) {
        this.items = items;
    }

    /**
     * Returns the URI associated with this converter.
     *
     * @return the uri
     */
    @XmlAttribute
    public URI getUri() {
        return uri;
    }

    /**
     * Returns a collection Question entities.
     *
     * @return a collection of Question entities
     */
    @XmlTransient
    public Collection<Question> getEntities() {
        entities = new ArrayList<Question>();
        if (items != null) {
            for (QuestionConverter item : items) {
                entities.add(item.getEntity());
            }
        }
        return entities;
    }
}
