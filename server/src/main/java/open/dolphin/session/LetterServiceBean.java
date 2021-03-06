package open.dolphin.session;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import open.dolphin.infomodel.LetterDate;
import open.dolphin.infomodel.LetterItem;
import open.dolphin.infomodel.LetterModule;
import open.dolphin.infomodel.LetterText;

/**
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
@Named
@Stateless
public class LetterServiceBean {
    
    private static final String KARTE_ID = "karteId";
    private static final String ID = "id";

    private static final String QUERY_LETTER_BY_KARTE_ID = "from LetterModule l where l.karte.id=:karteId";
    private static final String QUERY_LETTER_BY_ID = "from LetterModule l where l.id=:id";
    private static final String QUERY_ITEM_BY_ID = "from LetterItem l where l.module.id=:id";
    private static final String QUERY_TEXT_BY_ID = "from LetterText l where l.module.id=:id";
    private static final String QUERY_DATE_BY_ID = "from LetterDate l where l.module.id=:id";

    @PersistenceContext
    private EntityManager em;

    
    public long saveOrUpdateLetter(LetterModule model) {

        // 保存
        em.persist(model);
        List<LetterItem> items = model.getLetterItems();
        if (items != null) {
            for (LetterItem item : items) {
                item.setModule(model);
                em.persist(item);
            }
        }
        List<LetterText> texts = model.getLetterTexts();
        if (texts != null) {
            for (LetterText txt : texts) {
                txt.setModule(model);
                em.persist(txt);
            }
        }
        List<LetterDate> dates = model.getLetterDates();
        if (dates != null) {
            for (LetterDate date : dates) {
                date.setModule(model);
                em.persist(date);
            }
        }

        // 削除
        if (model.getLinkId()!=0L) {

            try {
                List<LetterItem> itemList = (List<LetterItem>)
                     em.createQuery(QUERY_ITEM_BY_ID)
                       .setParameter(ID, model.getLinkId())
                       .getResultList();
                for (LetterItem item : itemList) {
                    em.remove(item);
                }
            }catch(NoResultException e) {
                Logger.getLogger("open.dolphin").log(Level.WARNING, "QUERY_ITEM_BY_ID : {0}", new Object[]{e.toString()});
            }

            try {
                List<LetterText> textList = (List<LetterText>)
                     em.createQuery(QUERY_TEXT_BY_ID)
                       .setParameter(ID, model.getLinkId())
                       .getResultList();

                for (LetterText txt : textList) {
                    em.remove(txt);
                }
            }catch(NoResultException e) {
                Logger.getLogger("open.dolphin").log(Level.WARNING, "QUERY_TEXT_BY_ID : {0}", new Object[]{e.toString()});
            }

            try {
                List<LetterDate> dateList = (List<LetterDate>)
                     em.createQuery(QUERY_DATE_BY_ID)
                       .setParameter(ID, model.getLinkId())
                       .getResultList();

                for (LetterDate date : dateList) {
                    em.remove(date);
                }
            }catch(NoResultException e) {
                Logger.getLogger("open.dolphin").log(Level.WARNING, "QUERY_DATE_BY_ID : {0}", new Object[]{e.toString()});
            }

            try {
                LetterModule delete = (LetterModule)
                            em.createQuery(QUERY_LETTER_BY_ID)
                            .setParameter(ID, model.getLinkId())
                            .getSingleResult();
                em.remove(delete);
            }catch(NoResultException e) {
                Logger.getLogger("open.dolphin").log(Level.WARNING, "QUERY_LETTER_BY_ID : {0}", new Object[]{e.toString()});
            }
        }
        
        return model.getId();
    }

    
    public List<LetterModule> getLetterList(long karteId) {

        List<LetterModule> list = (List<LetterModule>)
                        em.createQuery(QUERY_LETTER_BY_KARTE_ID)
                        .setParameter(KARTE_ID, karteId)
                        .getResultList();
        return list;

    }

    
    public LetterModule getLetter(long letterPk) {

        LetterModule ret = (LetterModule)
                        em.createQuery(QUERY_LETTER_BY_ID)
                        .setParameter(ID, letterPk)
                        .getSingleResult();
        // item
        List<LetterItem> items = (List<LetterItem>)
                 em.createQuery(QUERY_ITEM_BY_ID)
                   .setParameter(ID, ret.getId())
                   .getResultList();
        ret.setLetterItems(items);

        // text
        List<LetterText> texts = (List<LetterText>)
                 em.createQuery(QUERY_TEXT_BY_ID)
                   .setParameter(ID, ret.getId())
                   .getResultList();
        ret.setLetterTexts(texts);

        // date
        List<LetterDate> dates = (List<LetterDate>)
                 em.createQuery(QUERY_DATE_BY_ID)
                   .setParameter(ID, ret.getId())
                   .getResultList();
        ret.setLetterDates(dates);

        return ret;
    }

    
    public void delete(long pk) {
        try {
            List<LetterItem> itemList = (List<LetterItem>)
                     em.createQuery(QUERY_ITEM_BY_ID)
                       .setParameter(ID, pk)
                       .getResultList();
            for (LetterItem item : itemList) {
                em.remove(item);
            }
        }catch(NoResultException e) {
            Logger.getLogger("open.dolphin").log(Level.WARNING, "QUERY_ITEM_BY_ID : {0}", new Object[]{e.toString()});
        }

        try {
            List<LetterText> textList = (List<LetterText>)
                 em.createQuery(QUERY_TEXT_BY_ID)
                   .setParameter(ID, pk)
                   .getResultList();

            for (LetterText txt : textList) {
                em.remove(txt);
            }
        }catch(NoResultException e) {
            Logger.getLogger("open.dolphin").log(Level.WARNING, "QUERY_TEXT_BY_ID : {0}", new Object[]{e.toString()});
        }

        try {
            List<LetterDate> dateList = (List<LetterDate>)
                 em.createQuery(QUERY_DATE_BY_ID)
                   .setParameter(ID, pk)
                   .getResultList();

            for (LetterDate date : dateList) {
                em.remove(date);
            }
        }catch(NoResultException e) {
            Logger.getLogger("open.dolphin").log(Level.WARNING, "QUERY_DATE_BY_ID : {0}", new Object[]{e.toString()});
        }

        try {
            LetterModule delete = (LetterModule)
                        em.createQuery(QUERY_LETTER_BY_ID)
                        .setParameter(ID, pk)
                        .getSingleResult();
            em.remove(delete);
        }catch(NoResultException e) {
            Logger.getLogger("open.dolphin").log(Level.WARNING, "QUERY_LETTER_BY_ID : {0}", new Object[]{e.toString()});
        }
    }
}
