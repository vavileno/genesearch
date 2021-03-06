package com.genesearch.repository;

import com.genesearch.model.Gene;
import com.genesearch.model.Homology;
import com.genesearch.model.SequenceFeature;
import com.genesearch.object.edit.HomologyEdit;
import com.genesearch.object.edit.SequenceFeatureEdit;
import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Created by user on 14.01.2015.
 */
@Repository
public class HomologyRepository extends  ModelRepository<Homology> {

    // Used for checking homology uniqueness when saving to database
    public Homology find(String primaryIdentifier, String symbol, String organismName, String type, String datasetsName, Long geneId) {
        Criteria c = getSession().createCriteria(getEntityClass(), "hm");
        c.createAlias("hm.gene", "gn", JoinType.INNER_JOIN);

        Conjunction and = new Conjunction();

        safeAddRestrictionEq(and, "gn.id", geneId);

        safeAddRestrictionEqOrNull(and, "hm.primaryIdentifier", primaryIdentifier);
        safeAddRestrictionEqOrNull(and, "hm.symbol", symbol);
        safeAddRestrictionEqOrNull(and, "hm.organismName", organismName);
        safeAddRestrictionEqOrNull(and, "hm.type", type);
        safeAddRestrictionEqOrNull(and, "hm.datasetsName", datasetsName);

        c.add(and);

        c.setProjection(Projections.countDistinct("hm.id"));
        long total = (Long) c.uniqueResult();

        c.setProjection(null);
        c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        List<Homology> result = c.list();

        if(result.size() == 0) {
            return null;
        }
        return result.get(0);
    }

    public List<Homology> find(Long geneId) {
        Criteria c = getSession().createCriteria(getEntityClass(), "hm");
        c.createAlias("hm.gene", "gn", JoinType.INNER_JOIN);
        Conjunction and = new Conjunction();
        and.add(Restrictions.eq("gn.id", geneId));
        c.add(and);
        c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return c.list();
    }

    // Removes homologies from database, related to selected gene
    // Method removes from database all homologies from that DOES NOT listed in an input list
    public void remove(Gene gene, List<HomologyEdit> homologyEditList) {
        Set<Long> remainIdList = new HashSet<Long>();
        Set<Homology> forDelete = new HashSet<Homology>();

        for(HomologyEdit homologyEdit : homologyEditList) {
            remainIdList.add(homologyEdit.getId());
        }

        List<Homology> homologyList =  find(gene.getId());
        for(Homology  homology : homologyList) {
            if(!remainIdList.contains(homology.getId())) {
                forDelete.add(homology);
            }
        }
        delete(forDelete);
    }



}
