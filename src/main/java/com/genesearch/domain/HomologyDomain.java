package com.genesearch.domain;

import com.genesearch.model.Gene;
import com.genesearch.model.Homology;
import com.genesearch.object.edit.HomologyEdit;
import com.genesearch.repository.HomologyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HomologyDomain {

    @Autowired
    private HomologyRepository homologyRepository;

    public List<HomologyEdit> find(Long geneId) {

        List<HomologyEdit> result = new ArrayList<HomologyEdit>();

        for(Homology homology : homologyRepository.find(geneId)) {
            result.add(HomologyEdit.create(homology));
        }
        return result;
    }

    public void update(Gene gene, List<HomologyEdit> homologyEditList) {
        homologyRepository.remove(gene, homologyEditList);

        for(HomologyEdit homologyEdit : homologyEditList) {
            if(homologyEdit.getId() != null) {
                update(gene, homologyEdit);
            }
            else {
                create(gene, homologyEdit);
            }
        }
    }

    public void update(Gene gene, HomologyEdit homologyEdit) {
        Homology homology = homologyRepository.findById(homologyEdit.getId());
        homology.update(gene, homologyEdit);
        homologyRepository.save(homology);
    }

    private void create(Gene gene, HomologyEdit homologyEdit) {
        Homology homology = new Homology();

        Homology h = homologyRepository.find(homologyEdit.getPrimaryIdentifier(), homologyEdit.getSymbol(), homologyEdit.getOrganismName(),
                homologyEdit.getType(), homologyEdit.getDatasetsName(), gene.getId());

        if(h == null) {
            homology.setGene(gene);
            homology.setPrimaryIdentifier(homologyEdit.getPrimaryIdentifier());
            homology.setSymbol(homologyEdit.getSymbol());
            homology.setOrganismName(homologyEdit.getOrganismName());
            homology.setType(homologyEdit.getType());
            homology.setDatasetsName(homologyEdit.getDatasetsName());

            homologyRepository.save(homology);
        }
    }
}
