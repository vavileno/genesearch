package com.genesearch.controller;

import com.genesearch.model.Gene;
import com.genesearch.object.request.SearchGeneRequest;
import com.genesearch.object.response.GeneResponse;
import com.genesearch.repository.GeneRepository;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by user on 31.12.2014.
 */
@Controller
@RequestMapping("/api")
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private GeneRepository geneRepository;

    @Transactional(readOnly = true)
    @RequestMapping(value = "/gene/search", method = RequestMethod.POST)
    @ResponseBody
    public Page<GeneResponse> showGene(@RequestBody SearchGeneRequest request) {
        List<GeneResponse> response = new ArrayList<GeneResponse>();
        response = geneRepository.search(request).getContent();
        return new PageImpl<GeneResponse>(response, request, response.size());
    }

    @Transactional(readOnly = true)
    @RequestMapping(value = "/gene/show", method = RequestMethod.GET)
    @ResponseBody
    public GeneResponse showGene() {
        GeneResponse response = new GeneResponse();

        Gene gene = geneRepository.show(1L);

        response.setId(gene.getId());
        response.setName(gene.getName());

        return response;
    }
}
