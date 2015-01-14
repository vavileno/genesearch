package com.genesearch.webservice;

import org.intermine.metadata.Model;
import org.intermine.pathquery.Constraints;
import org.intermine.pathquery.OrderDirection;
import org.intermine.pathquery.PathQuery;
import org.intermine.webservice.client.core.ServiceFactory;
import org.intermine.webservice.client.services.QueryService;

import java.util.List;

/**
 * Created by user on 14.01.2015.
 */
public class GeneDetailsRetriever implements WebServiceRetriever {

    private String geneId;

    public GeneDetailsRetriever(String geneId) {
        this.geneId = geneId;
    }

    @Override
    public List<List<Object>> execute() {
        ServiceFactory factory = new ServiceFactory(ROOT);
        Model model = factory.getModel();
        PathQuery query = new PathQuery(model);

        // Select the output columns:
        query.addViews("OntologyAnnotation.subject.primaryIdentifier",
                "OntologyAnnotation.subject.symbol",
                "OntologyAnnotation.subject.name",
                "OntologyAnnotation.ontologyTerm.name",
                "OntologyAnnotation.subject.description",
                "OntologyAnnotation.evidence.publications.pubMedId",
                "OntologyAnnotation.ontologyTerm.identifier",
                "OntologyAnnotation.evidence.baseAnnotations.subject.background.name",
                "OntologyAnnotation.evidence.baseAnnotations.subject.zygosity",
                "OntologyAnnotation.evidence.publications.doi",
                "OntologyAnnotation.subject.chromosome.name");

        // Add orderby
        query.addOrderBy("OntologyAnnotation.subject.symbol", OrderDirection.ASC);

        // Select the output columns:
        query.addViews("Gene.primaryIdentifier",
                "Gene.symbol",
                "OntologyAnnotation.subject.name",
                "OntologyAnnotation.ontologyTerm.name",
                "OntologyAnnotation.subject.description",
                "OntologyAnnotation.evidence.publications.pubMedId",
                "OntologyAnnotation.ontologyTerm.identifier",
                "OntologyAnnotation.evidence.baseAnnotations.subject.background.name",
                "OntologyAnnotation.evidence.baseAnnotations.subject.zygosity",
                "OntologyAnnotation.evidence.publications.doi",
                "OntologyAnnotation.subject.chromosome.name",

                "Gene.organism.name",
                "Gene.homologues.homologue.primaryIdentifier",
                "Gene.homologues.homologue.symbol",
                "Gene.homologues.homologue.organism.name",
                "Gene.homologues.type",
                "Gene.homologues.dataSets.name",
                "Gene.ncbiGeneNumber");

        // Add orderby
        query.addOrderBy("Gene.primaryIdentifier", OrderDirection.ASC);

        // Filter the results with the following constraints:
        query.addConstraint(Constraints.type("Gene.ontologyAnnotations.ontologyTerm.parents", "MPTerm"));
        query.addConstraint(Constraints.type("Gene.ontologyAnnotations.evidence.baseAnnotations.subject", "Genotype"));
        query.addConstraint(Constraints.type("Gene.ontologyAnnotations.ontologyTerm", "MPTerm"));
        query.addConstraint(Constraints.lookup("Gene.ontologyAnnotations.ontologyTerm.parents", "MP:0001924", null), "A");
        query.addConstraint(Constraints.equalToLoop("Gene", "Gene.ontologyAnnotations.subject"), "B");
        // Specify how these constraints should be combined.
        query.setConstraintLogic("A and B");

        QueryService service = factory.getQueryService();
        return service.getRowsAsLists(query);
    }

}