package com.genesearch.webservice;

import org.intermine.metadata.Model;
import org.intermine.pathquery.Constraints;
import org.intermine.pathquery.OrderDirection;
import org.intermine.pathquery.PathQuery;
import org.intermine.webservice.client.core.ServiceFactory;
import org.intermine.webservice.client.services.QueryService;

import java.util.List;

public class GeneRetriever implements WebServiceRetriever {

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

        // Filter the results with the following constraints:
        query.addConstraint(Constraints.type("OntologyAnnotation.ontologyTerm.parents", "MPTerm"));
        query.addConstraint(Constraints.type("OntologyAnnotation.ontologyTerm", "MPTerm"));
        query.addConstraint(Constraints.type("OntologyAnnotation.subject", "SequenceFeature"));
        query.addConstraint(Constraints.type("OntologyAnnotation.evidence.baseAnnotations.subject", "Genotype"));
        query.addConstraint(Constraints.lookup("OntologyAnnotation.ontologyTerm.parents", "MP:0001924", null), "A");

        QueryService service = factory.getQueryService();

        return service.getRowsAsLists(query);
    }

}
