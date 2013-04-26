
package org.n52.server.service.rest.control;

import static org.n52.shared.requests.query.QueryParameters.createEmptyFilterQuery;

import org.n52.server.service.rest.model.ModelAndViewPager;
import org.n52.shared.requests.query.QueryFactory;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.requests.query.ResultPage;
import org.n52.shared.requests.query.queries.QueryRequest;
import org.n52.shared.requests.query.responses.QueryResponse;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/services", produces = {"text/html", "application/*"})
public class RestfulOfferingsController extends TimeseriesParameterController implements RestfulKvp, RestfulUrls {

    @RequestMapping(value = "/{instance}/" + PATH_OFFERINGS)
    public ModelAndView getProcedureByGET(@PathVariable("instance") String instance, 
                                          @RequestParam(value = KVP_SHOW, required = false) String details, 
                                          @RequestParam(value = KVP_OFFSET, required = false) Integer offset, 
                                          @RequestParam(value = KVP_SIZE, required = false, defaultValue = KVP_DEFAULT_SIZE) Integer size) throws Exception {

        // TODO condense output depending on 'show' parameter

        QueryParameters parameters = createEmptyFilterQuery();
        QueryResponse< ? > result = performQuery(instance, parameters);
        Offering[] offerings = (Offering[]) result.getResults();

        if (offset == null) {
            ModelAndView mav = new ModelAndView("offerings");
            return mav.addObject(offerings);
        }
        else {
            ModelAndViewPager mavPage = createResultPage(offset.intValue(), size.intValue(), offerings);
            return mavPage.getPagedModelAndView();
        }
    }
    
    @RequestMapping(value = "/{instance}/" + PATH_OFFERINGS + "/{id}")
    public ModelAndView getProcedureByID(@PathVariable(value = "instance") String instance, 
                                         @PathVariable(value = "id") String offering) throws Exception {
        ModelAndView mav = new ModelAndView("offerings");
        QueryParameters parameters = new QueryParameters().setOffering(offering);
        QueryResponse< ? > result = performQuery(instance, parameters);
        
        Offering[] offerings = (Offering[]) result.getResults();
        if (offerings.length == 0) {
            throw new ResourceNotFoundException();
        } else {
            mav.addObject(offerings[0]);
        }
        return mav;
    }

    private ModelAndViewPager createResultPage(int offset, int size, Offering[] offerings) {
        ModelAndViewPager mavPage = new ModelAndViewPager("offerings");
        mavPage.setPage(ResultPage.createPageFrom(offerings, offset, size));
        return mavPage;
    }

    @Override
    protected QueryResponse< ? > performQuery(String instance, QueryParameters parameters) throws Exception {
        QueryFactory factory = getQueryFactoryFor(instance);
        QueryRequest query = factory.createFilteredOfferingQuery(parameters);
        return doQuery(query);
    }

}