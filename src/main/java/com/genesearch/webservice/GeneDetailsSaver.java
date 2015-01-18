package com.genesearch.webservice;

import com.genesearch.model.*;
import com.genesearch.repository.GeneHomologueRepository;
import com.genesearch.repository.GeneRepository;
import com.genesearch.repository.HomologueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by kmorozov on 15.01.2015.
 */
@Service
public class GeneDetailsSaver implements DbSaver {

    @Autowired
    private HomologueRepository homologueRepository;
    @Autowired
    private GeneRepository geneRepository;
    @Autowired
    private GeneHomologueRepository geneHomologueRepository;

    @Override
    public void execute(WebServiceRetriever retriever) {
        List<List<Object>> result = retriever.execute();

        for(List<Object> row : result) {
            Gene gene = new Gene();
            Homologue homologue = new Homologue();

            gene.setPrimaryIdentifier(safeString(row.get(0)));
            gene.setSymbol(safeString(row.get(1)));
            gene.setOrganismName(safeString(row.get(2)));
            homologue.setPrimaryIdentifier(safeString(row.get(3)));
            homologue.setSymbol(safeString(row.get(4)));
            homologue.setOrganismName(safeString(row.get(5)));
            homologue.setType(safeString(row.get(6)));
            homologue.setDatasetsName(safeString(row.get(7)));
            gene.setNcbi(safeString(row.get(8)));

            Homologue homologueFromDb = homologueRepository.find(homologue.getPrimaryIdentifier(), homologue.getSymbol(),
                    homologue.getOrganismName(), homologue.getType(), homologue.getDatasetsName());
            if(homologueFromDb == null) {
                homologueRepository.save(homologue);
            }
            else {
                homologue = homologueFromDb;
            }


            Gene geneFromDb = geneRepository.find(gene.getPrimaryIdentifier(), gene.getSymbol(), gene.getOrganismName(), gene.getNcbi());
            if(geneFromDb == null) {
                geneRepository.save(gene);
            }
            else {
                gene = geneFromDb;
            }

            GeneHomologue gh = new GeneHomologue();
            gh.setGene(gene);
            gh.setHomologue(homologue);

            GeneHomologue ghFromDb = geneHomologueRepository.findOne(gene.getId(), homologue.getId());
            if(ghFromDb == null) {
                geneHomologueRepository.save(gh);
            }

        }
    }

    private String safeString(Object object) {
        String result = null;

        if(object == null) {
            return null;
        }

        if(object instanceof String) {
            result = (String) object;
            if(result.trim().equalsIgnoreCase("null")) {
                result = null;
            }
        }
        else {
            result = object.toString();
        }

        return object.toString();
    }
}