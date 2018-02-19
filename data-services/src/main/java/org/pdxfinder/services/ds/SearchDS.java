package org.pdxfinder.services.ds;

import org.pdxfinder.dao.ModelCreation;
import org.pdxfinder.dao.OntologyTerm;
import org.pdxfinder.dao.Specimen;
import org.pdxfinder.repositories.ModelCreationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/*
 * Created by csaba on 19/01/2018.
 */

@Component
public class SearchDS {

    private final static Logger log = LoggerFactory.getLogger(SearchDS.class);

    private Set<ModelForQuery> models;
    private Map<String, String> cancerSystemMap = new HashMap<>();

    public static List<String> PATIENT_AGE_OPTIONS = Arrays.asList(
            "0-9",
            "10-19",
            "20-29",
            "30-39",
            "40-49",
            "50-59",
            "60-69",
            "70-79",
            "80-89",
            "90",
            "Not Specified"
    );
    public static List<String> DATASOURCE_OPTIONS = Arrays.asList(
            "JAX",
            "IRCC",
            "PDMR",
            "PDXNet-HCI-BCM",
            "PDXNet-MDAnderson",
            "PDXNet-WUSTL",
            "PDXNet-Wistar-MDAnderson-Penn"
    );
    public static List<String> PATIENT_GENDERS = Arrays.asList(
            "Male",
            "Female",
            "Not Specified"
    );


    public static List<String> CANCERS_BY_SYSTEM_OPTIONS = Arrays.asList(
            "Breast Cancer",
            "Cardiovascular Cancer",
            "Connective and Soft Tissue Cancer",
            "Digestive System Cancer",
            "Endocrine Cancer",
            "Eye Cancer",
            "Head and Neck Cancer",
            "Hematopoietic and Lymphoid System Cancer",
            "Nervous System Cancer",
            "Peritoneal and Retroperitoneal Cancer",
            "Reproductive System Cancer",
            "Respiratory Tract Cancer",
            "Thoracic Cancer",
            "Skin Cancer",
            "Urinary System Cancer",
            "Unclassified"
    );

    public static List<String> SAMPLE_TUMOR_TYPE_OPTIONS = Arrays.asList(
            "Primary",
            "Metastatic",
            "Recurrent",
            "Refractory",
            "Not Specified"
            );
    public static List<String> DIAGNOSIS_OPTIONS = new ArrayList<>();

    /**
     * Populate the complete set of models for searching when this object is instantiated
     */
    public SearchDS(ModelCreationRepository modelCreationRepository) {
        Assert.notNull(modelCreationRepository, "Model repository cannot be null");
        this.models = new HashSet<>();

        // Mapping NCIT ontology term labels to display labels
        this.cancerSystemMap.put("Breast Cancer", "Breast Cancer");
        this.cancerSystemMap.put("Cardiovascular Cancer", "Cardiovascular Cancer");
        this.cancerSystemMap.put("Connective and Soft Tissue Neoplasm", "Connective and Soft Tissue Cancer");
        this.cancerSystemMap.put("Digestive System Cancer", "Digestive System Cancer");
        this.cancerSystemMap.put("Endocrine Cancer", "Endocrine Cancer");
        this.cancerSystemMap.put("Eye Cancer", "Eye Cancer");
        this.cancerSystemMap.put("Head and Neck Cancer", "Head and Neck Cancer");
        this.cancerSystemMap.put("Hematopoietic and Lymphoid System Neoplasm", "Hematopoietic and Lymphoid System Cancer");
        this.cancerSystemMap.put("Nervous System Cancer", "Nervous System Cancer");
        this.cancerSystemMap.put("Peritoneal and Retroperitoneal Neoplasms", "Peritoneal and Retroperitoneal Cancer");
        this.cancerSystemMap.put("Reproductive System Neoplasm", "Reproductive System Cancer");
        this.cancerSystemMap.put("Respiratory Tract Cancer", "Respiratory Tract Cancer");
        this.cancerSystemMap.put("Thoracic Neoplasm", "Thoracic Cancer");
        this.cancerSystemMap.put("Skin Cancer", "Skin Cancer");
        this.cancerSystemMap.put("Urinary System Cancer", "Urinary System Cancer");
        this.cancerSystemMap.put("Unclassified", "Unclassified");


        //
        // When this class is instantiated,
        //   populate and cache the models set
        //

        for (ModelCreation mc : modelCreationRepository.getModelsWithPatientData()) {

            ModelForQuery mfq = new ModelForQuery();
            mfq.setModelId(mc.getId());
            mfq.setExternalId(mc.getSourcePdxId());
            mfq.setDatasource(mc.getDataSource());

            if (mc.getSample().getPatientSnapshot().getTreatmentNaive() != null) {
                mfq.setTreatmentHistory(mc.getSample().getPatientSnapshot().getTreatmentNaive().toString());
            } else {
                mfq.setTreatmentHistory("Not Specified");
            }

            if (mc.getSample().getSampleSite() != null) {
                mfq.setSampleSampleSite(mc.getSample().getSampleSite().getName());
            } else {
                mfq.setSampleSampleSite("Not Specified");
            }

            if (mc.getSample().getType() != null) {
                mfq.setSampleTumorType(mc.getSample().getType().getName());
            } else {
                mfq.setSampleTumorType("Not Specified");
            }

            if (mc.getSample().getSampleSite() != null) {
                mfq.setSampleSampleSite(mc.getSample().getSampleSite().getName());
            } else {
                mfq.setSampleSampleSite("Not Specified");
            }

            // Patient information
            mfq.setPatientAge(mc.getSample().getPatientSnapshot().getAgeBin());
            mfq.setPatientGender(mc.getSample().getPatientSnapshot().getPatient().getSex());
            mfq.setDiagnosis(mc.getSample().getDiagnosis());
            mfq.setMappedOntologyTerm(mc.getSample().getSampleToOntologyRelationShip().getOntologyTerm().getLabel());

            if (mc.getSample().getPatientSnapshot().getTreatmentNaive() != null) {
                mfq.setPatientTreatmentStatus(mc.getSample().getPatientSnapshot().getTreatmentNaive().toString());
            }

            // Sample information
            mfq.setSampleExtractionMethod(mc.getSample().getExtractionMethod());
            mfq.setSampleOriginTissue(mc.getSample().getOriginTissue().getName());
            mfq.setSampleClassification(mc.getSample().getClassification());

            if (mc.getSample().getType() != null) {
                mfq.setSampleTumorType(mc.getSample().getType().getName());
            }
            // Model information
            Set<Specimen> specimens = mc.getSpecimens();
            Set<String> hoststrains = new HashSet<>();
            if (specimens != null && specimens.size() > 0) {

                for (Specimen s: specimens){
                    hoststrains.add(s.getHostStrain().getName());

                    mfq.setModelImplantationSite(s.getImplantationSite().getName());
                    mfq.setModelImplantationType(s.getImplantationType().getName());
                }
                //Specimen s = specimens.iterator().next();
                mfq.setModelHostStrain(hoststrains);
            }

            // Get all ancestor ontology terms (including self) into a set specific for this model
            Set<OntologyTerm> allOntologyTerms = new HashSet<>();

            // Add direct mapped term
            allOntologyTerms.add(mc.getSample().getSampleToOntologyRelationShip().getOntologyTerm());

            // Add all ancestors of direct mapped term
            for (OntologyTerm t : mc.getSample().getSampleToOntologyRelationShip().getOntologyTerm().getSubclassOf()) {
                allOntologyTerms.addAll(getAllAncestors(t));
            }

            mfq.setAllOntologyTermAncestors(allOntologyTerms.stream().map(OntologyTerm::getLabel).collect(Collectors.toSet()));

            // Add all top level systems (translated) to the Model
            for (String s : allOntologyTerms.stream().map(OntologyTerm::getLabel).collect(Collectors.toSet())) {

                if (this.cancerSystemMap.keySet().contains(s)) {

                    if (mfq.getCancerSystem() == null) {
                        mfq.setCancerSystem(new ArrayList<>());
                    }

                    mfq.getCancerSystem().add(this.cancerSystemMap.get(s));

                }
            }

            // Ensure that ALL models have a system -- even if it's not in the ontology nodes specified
            if (mfq.getCancerSystem() == null || mfq.getCancerSystem().size() == 0) {
                if (mfq.getCancerSystem() == null) {
                    mfq.setCancerSystem(new ArrayList<>());
                }

                mfq.getCancerSystem().add(this.cancerSystemMap.get("Unclassified"));

            }

            // TODO: Complete the organ options
            // TODO: Complete the cell type options
            // TODO: Complete the patient treatment options


            models.add(mfq);
        }


        // Populate the list of possible diagnoses
        DIAGNOSIS_OPTIONS = models.stream().map(ModelForQuery::getDiagnosis).distinct().collect(Collectors.toList());


        //
        // Filter out all options that have no models for that option
        //
        PATIENT_AGE_OPTIONS = PATIENT_AGE_OPTIONS
                .stream()
                .filter(x -> models
                        .stream()
                        .map(ModelForQuery::getPatientAge)
                        .collect(Collectors.toSet())
                        .contains(x))
                .collect(Collectors.toList());

        DATASOURCE_OPTIONS = DATASOURCE_OPTIONS
                .stream()
                .filter(x -> models
                        .stream()
                        .map(ModelForQuery::getDatasource)
                        .collect(Collectors.toSet())
                        .contains(x))
                .collect(Collectors.toList());

        CANCERS_BY_SYSTEM_OPTIONS = CANCERS_BY_SYSTEM_OPTIONS
                .stream()
                .filter(x -> models
                        .stream()
                        .map(ModelForQuery::getCancerSystem)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet())
                        .contains(x))
                .collect(Collectors.toList());

        PATIENT_GENDERS = PATIENT_GENDERS
                .stream()
                .filter(x -> models
                        .stream()
                        .map(ModelForQuery::getPatientGender)
                        .collect(Collectors.toSet())
                        .contains(x))
                .collect(Collectors.toList());

        SAMPLE_TUMOR_TYPE_OPTIONS = SAMPLE_TUMOR_TYPE_OPTIONS
                .stream()
                .filter(x -> models
                        .stream()
                        .map(ModelForQuery::getSampleTumorType)
                        .collect(Collectors.toSet())
                        .contains(x))
                .collect(Collectors.toList());

        DIAGNOSIS_OPTIONS = DIAGNOSIS_OPTIONS
                .stream()
                .filter(x -> models
                        .stream()
                        .map(ModelForQuery::getDiagnosis)
                        .collect(Collectors.toSet())
                        .contains(x))
                .collect(Collectors.toList());

    }

    /**
     * Recursively get all ancestors starting from the supplied ontology term
     *
     * @param t the starting term in the ontology
     * @return a set of ontology terms corresponding to the ancestors of the term supplied
     */
    public Set<OntologyTerm> getAllAncestors(OntologyTerm t) {

        Set<OntologyTerm> retSet = new HashSet<>();

        // Store this ontology term in the set
        retSet.add(t);

        // If this term has parent terms
        if (t.getSubclassOf() != null && t.getSubclassOf().size() > 0) {

            // For each parent term
            for (OntologyTerm st : t.getSubclassOf()) {

                // Recurse and add all ancestor terms to the set
                retSet.addAll(getAllAncestors(st));
            }
        }

        // Return the full set
        return retSet;
    }

    public Set<ModelForQuery> getModels() {
        return models;
    }

    public void setModels(Set<ModelForQuery> models) {
        this.models = models;
    }


    /**
     * Search function accespts a Map of key value pairs
     * key = what facet to search
     * list of values = what values to filter on (using OR)
     * <p>
     * EX of expected data structure:
     * <p>
     * patient_age -> { 5-10, 20-40 },
     * patient_gender -> { Male },
     * sample_origin_tissue -> { Lung, Liver }
     * <p>
     * would yield results for male patients between 5-10 OR between 20-40 AND that had cancers in the lung OR liver
     *
     * @param filters
     * @return set of models derived from filtering the complete set according to the
     * filters passed in as arguments
     */
    public Set<ModelForQuery> search(Map<SearchFacetName, List<String>> filters) {

        Set<ModelForQuery> result = new HashSet<>(models);

        // If no filters have been specified, return the complete set
        if (filters == null) {
            return result;
        }

        for (SearchFacetName facet : filters.keySet()) {

            Predicate predicate;

            switch (facet) {

                case query:

                    predicate = getContainsMatchDisjunctionPredicate(filters.get(SearchFacetName.query));

                    Set<ModelForQuery> accumulate = new HashSet<>();
                    for (ModelForQuery r : result) {

                        Set<String> i = r.getAllOntologyTermAncestors().stream().filter(x -> predicate.test(x)).collect(Collectors.toSet());
                        if (i != null && i.size() > 0) {
                            r.setQueryMatch(i);
                            accumulate.add(r);
                        }

                    }

                    result = accumulate;
                    break;

                case datasource:

                    predicate = getExactMatchDisjunctionPredicate(filters.get(SearchFacetName.datasource));
                    result = result.stream().filter(x -> predicate.test(x.getDatasource())).collect(Collectors.toSet());
                    break;

                case diagnosis:

                    predicate = getExactMatchDisjunctionPredicate(filters.get(SearchFacetName.diagnosis));
                    result = result.stream().filter(x -> predicate.test(x.getMappedOntologyTerm())).collect(Collectors.toSet());
                    break;

                case patient_age:

                    predicate = getExactMatchDisjunctionPredicate(filters.get(SearchFacetName.patient_age));
                    result = result.stream().filter(x -> predicate.test(x.getPatientAge())).collect(Collectors.toSet());
                    break;

                case patient_treatment_status:

                    predicate = getExactMatchDisjunctionPredicate(filters.get(SearchFacetName.patient_treatment_status));
                    result = result.stream().filter(x -> predicate.test(x.getPatientTreatmentStatus())).collect(Collectors.toSet());
                    break;

                case patient_gender:

                    predicate = getExactMatchDisjunctionPredicate(filters.get(SearchFacetName.patient_gender));
                    result = result.stream().filter(x -> predicate.test(x.getPatientGender())).collect(Collectors.toSet());
                    break;

                case sample_origin_tissue:

                    predicate = getExactMatchDisjunctionPredicate(filters.get(SearchFacetName.sample_origin_tissue));
                    result = result.stream().filter(x -> predicate.test(x.getSampleOriginTissue())).collect(Collectors.toSet());
                    break;

                case sample_classification:

                    predicate = getExactMatchDisjunctionPredicate(filters.get(SearchFacetName.sample_classification));
                    result = result.stream().filter(x -> predicate.test(x.getSampleClassification())).collect(Collectors.toSet());
                    break;

                case sample_tumor_type:

                    predicate = getExactMatchDisjunctionPredicate(filters.get(SearchFacetName.sample_tumor_type));
                    result = result.stream().filter(x -> predicate.test(x.getSampleTumorType())).collect(Collectors.toSet());
                    break;

                case model_implantation_site:

                    predicate = getExactMatchDisjunctionPredicate(filters.get(SearchFacetName.model_implantation_site));
                    result = result.stream().filter(x -> predicate.test(x.getModelImplantationSite())).collect(Collectors.toSet());
                    break;

                case model_implantation_type:

                    predicate = getExactMatchDisjunctionPredicate(filters.get(SearchFacetName.model_implantation_type));
                    result = result.stream().filter(x -> predicate.test(x.getModelImplantationType())).collect(Collectors.toSet());
                    break;

                case model_host_strain:

                    predicate = getExactMatchDisjunctionPredicate(filters.get(SearchFacetName.model_host_strain));
                    result = result.stream().filter(x -> predicate.test(x.getModelHostStrain())).collect(Collectors.toSet());
                    break;

                case cancer_system:

                    Set<ModelForQuery> toRemove = new HashSet<>();
                    for (ModelForQuery res : result) {
                        Boolean keep = Boolean.FALSE;
                        for (String s : filters.get(SearchFacetName.cancer_system)) {
                            if (res.getCancerSystem().contains(s)) {
                                keep = Boolean.TRUE;
                            }
                        }
                        if (!keep) {
                            toRemove.add(res);
                        }
                    }

                    result.removeAll(toRemove);
                    break;

                case organ:

                    predicate = getExactMatchDisjunctionPredicate(filters.get(SearchFacetName.organ));
                    result = result.stream().filter(x -> predicate.test(x.getCancerOrgan())).collect(Collectors.toSet());
                    break;

                case cell_type:

                    predicate = getExactMatchDisjunctionPredicate(filters.get(SearchFacetName.cell_type));
                    result = result.stream().filter(x -> predicate.test(x.getCancerCellType())).collect(Collectors.toSet());
                    break;

                default:
                    // default case is an unexpected filter option
                    // Do not filter anything
                    log.info("Unrecognised facet {} passed to search, skipping.", facet);
                    break;
            }
        }

        return result;
    }


    /**
     * getExactMatchDisjunctionPredicate returns a composed predicate with all the supplied filters "OR"ed together
     * using an exact match
     * <p>
     * NOTE: This is a case sensitive match!
     *
     * @param filters the set of strings to match against
     * @return a composed predicate case insensitive matching the supplied filters using disjunction (OR)
     */
    Predicate<String> getExactMatchDisjunctionPredicate(List<String> filters) {
        List<Predicate<String>> preds = new ArrayList<>();

        // Iterate through the filter options passed in for this facet
        for (String filter : filters) {

            // Create a filter predicate for each option
            Predicate<String> pred = s -> s.equals(filter);

            // Store all filter options in a list
            preds.add(pred);
        }

        // Create a "combination" predicate containing sub-predicates "OR"ed together
        return preds.stream().reduce(Predicate::or).orElse(x -> false);
    }

    /**
     * getContainsMatchDisjunctionPredicate returns a composed predicate with all the supplied filters "OR"ed together
     * using a contains match
     * <p>
     * NOTE: This is a case insensitive match!
     *
     * @param filters the set of strings to match against
     * @return a composed predicate case insensitive matching the supplied filters using disjunction (OR)
     */
    Predicate getContainsMatchDisjunctionPredicate(List<String> filters) {
        List<Predicate<String>> preds = new ArrayList<>();

        // Iterate through the filter options passed in for this facet
        for (String filter : filters) {

            // Create a filter predicate for each option
            Predicate<String> pred = s -> s.toLowerCase().contains(filter.toLowerCase());

            // Store all filter options in a list
            preds.add(pred);
        }

        // Create a "combination" predicate containing sub-predicates "OR"ed together
        return preds.stream().reduce(Predicate::or).orElse(x -> false);
    }


    /**
     * Get the count of models for a supplied facet.
     * <p>
     * This method will return counts of facet options for the supplied facet
     *
     * @param facet    the facet to count
     * @param results  set of models already filtered
     * @param selected what facets have been filtered already
     * @return a list of {@link FacetOption} indicating counts and selected state
     */
    @Cacheable("facet_counts")
    public List<FacetOption> getFacetOptions(SearchFacetName facet, List<String> options, Set<ModelForQuery> results, List<String> selected) {

        Set<ModelForQuery> allResults = models;

        List<FacetOption> map = new ArrayList<>();

        // Initialise all facet option counts to 0 and set selected attribute on all options that the user has chosen
        if (options != null) {
            for (String option : options) {
                Long count = allResults
                        .stream()
                        .filter(x ->
                                Stream.of(x.getBy(facet).split("::"))
                                        .collect(Collectors.toSet())
                                        .contains(option))
                        .count();
                map.add(new FacetOption(option, selected != null ? 0 : count.intValue(), count.intValue(), selected != null && selected.contains(option) ? Boolean.TRUE : Boolean.FALSE, facet));
            }
        }

        // We want the counts on the facets to look something like:
        // Gender
        //   [ ] Male (1005 of 1005)
        //   [ ] Female (840 of 840)
        //   [ ] Not specified (31 of 31)
        // Then when a facet is clicked:
        // Gender
        //   [X] Male (1005 of (1005)
        //   [ ] Female (0 of 840)
        //   [ ] Not specified (2 of 31)
        //


        // Iterate through results adding count to the appropriate option
        for (ModelForQuery mfq : results) {

            String s = mfq.getBy(facet);

            // Skip empty facets
            if (s == null || s.equals("")) {
                continue;
            }

            // List of ontology terms may come from the service.  These will by separated by "::" delimiter
            if (s.contains("::")) {

                for (String ss : s.split("::")) {

                    // There should be only one element per facet name
                    map.forEach(x -> {
                        if (x.getName().equals(ss)) {
                            x.increment();
                        }
                    });
                }

            } else {

                // Initialise on the first time we see this facet name
                if (map.stream().noneMatch(x -> x.getName().equals(s))) {
                    map.add(new FacetOption(s, 0, 0, selected != null && selected.contains(s) ? Boolean.TRUE : Boolean.FALSE, facet));
                }

                // There should be only one element per facet name
                map.forEach(x -> {
                    if (x.getName().equals(s)) {
                        x.increment();
                    }
                });
            }
        }


//        Collections.sort(map);

        return map;
    }


}