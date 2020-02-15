package org.pdxfinder.services;


import org.pdxfinder.dao.*;
import org.pdxfinder.repositories.*;
import org.pdxfinder.services.dto.DetailsDTO;
import org.pdxfinder.services.dto.DrugSummaryDTO;
import org.pdxfinder.services.dto.SearchDTO;
import org.pdxfinder.services.dto.VariationDataDTO;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchService {


    private SampleRepository sampleRepository;

    private PatientRepository patientRepository;
    private PatientSnapshotRepository patientSnapshotRepository;
    private ModelCreationRepository modelCreationRepository;
    private OntologyTermRepository ontologyTermRepositoryRepository;
    private SpecimenRepository specimenRepository;
    private MolecularCharacterizationRepository molecularCharacterizationRepository;
    private PlatformRepository platformRepository;
    private TreatmentSummaryRepository treatmentSummaryRepository;

    private final String JAX_URL = "http://tumor.informatics.jax.org/mtbwi/pdxDetails.do?modelID=";
    private final String JAX_URL_TEXT = "View data at JAX";
    private final String IRCC_URL = "mailto:andrea.bertotti@unito.it?subject=";
    private final String IRCC_URL_TEXT = "Contact IRCC here";
    private final String PDMR_URL = "https://pdmdb.cancer.gov/pls/apex/f?p=101:41";
    private final String PDMR_URL_TEXT = "Access PDMR here";
    
    // for PDXNet
    //private final String HCI_URL = "https://www.pdxnetwork.org/hcibcm/";
    private final String HCI_URL = "";
    private final String HCI_DS = "PDXNet-HCI-BCM";
//    private final String WISTAR_URL = "https://www.pdxnetwork.org/the-wistarmd-andersonpenn/";
    private final String WISTAR_URL = "";

    private final String WISTAR_DS = "PDXNet-Wistar-MDAnderson-Penn";
    private final String MDA_URL = "";
    //private final String MDA_URL = "https://www.pdxnetwork.org/md-anderson/";
    private final String MDA_DS = "PDXNet-MDAnderson";
    //private final String WUSTL_URL = "https://www.pdxnetwork.org/wustl/";
    private final String WUSTL_URL = "";
    private final String WUSTL_DS = "PDXNet-WUSTL";



    public SearchService(SampleRepository sampleRepository,
                         PatientRepository patientRepository,
                         PatientSnapshotRepository patientSnapshotRepository,
                         ModelCreationRepository modelCreationRepository,
                         OntologyTermRepository ontologyTermRepository,
                         SpecimenRepository specimenRepository,
                         MolecularCharacterizationRepository molecularCharacterizationRepository,
                         PlatformRepository platformRepository,
                         TreatmentSummaryRepository treatmentSummaryRepository) {
        this.sampleRepository = sampleRepository;
        this.patientRepository = patientRepository;
        this.patientSnapshotRepository = patientSnapshotRepository;
        this.modelCreationRepository = modelCreationRepository;
        this.ontologyTermRepositoryRepository = ontologyTermRepository;
        this.molecularCharacterizationRepository = molecularCharacterizationRepository;
        this.specimenRepository = specimenRepository;
        this.platformRepository = platformRepository;
        this.treatmentSummaryRepository = treatmentSummaryRepository;
    }


    public int modelCount() {

        int pdxCount = modelCreationRepository.countAllModels();
        return  pdxCount;

    }


    public Boolean isExistingOntologyTerm(String query) {

        if (ontologyTermRepositoryRepository.findByLabel(query) != null) return true;
        return false;

    }


    public List<SearchDTO> searchWithOntology(String query, String[] markers, String[] datasources, String[] origintumortypes) {

        Collection<ModelCreation> models = modelCreationRepository.findByOntology(query, markers, datasources, origintumortypes);

        return createSearchResult(models, query);

    }

    public List<SearchDTO> searchForModelsWithFilters(String diag, String[] markers, String[] datasources, String[] origintumortypes) {

        Collection<ModelCreation> models = modelCreationRepository.findByMultipleFilters(diag, markers, datasources, origintumortypes);

        return createSearchResult(models, diag);
    }

    public List<SearchDTO> search(String query, String[] markers, String[] datasources, String[] origintumortypes) {

        if (isExistingOntologyTerm(query)) return searchWithOntology(query, markers, datasources, origintumortypes);

        else return searchForModelsWithFilters(query, markers, datasources, origintumortypes);

    }


    public List<SearchDTO> createSearchResult(Collection<ModelCreation> models, String query) {

        List<SearchDTO> results = new ArrayList<>();

        for (ModelCreation model : models) {

            SearchDTO sdto = new SearchDTO();

            if (model.getSourcePdxId() != null) {
                sdto.setModelId(model.getSourcePdxId());
            }

            if (model.getSample() != null && model.getSample().getDataSource() != null) {
                sdto.setDataSource(model.getSample().getDataSource());
            }

            if (model.getSample() != null && model.getSample().getSourceSampleId() != null) {
                sdto.setTumorId(model.getSample().getSourceSampleId());
            }

            if (model.getSample() != null && model.getSample().getDiagnosis() != null) {
                sdto.setDiagnosis(model.getSample().getDiagnosis());
            }

            if (model.getSample() != null && model.getSample().getOriginTissue() != null) {
                sdto.setTissueOfOrigin(model.getSample().getOriginTissue().getName());
            }

            if (model.getSample() != null && model.getSample().getType() != null) {
                sdto.setTumorType(model.getSample().getType().getName());
            }

            if (model.getSample() != null && model.getSample().getClassification() != null) {
                sdto.setClassification(model.getSample().getClassification());
            }

            if (model.getSample() != null && model.getSample().getSampleToOntologyRelationShip() != null) {
                sdto.setMappedOntology(model.getSample().getSampleToOntologyRelationShip().getOntologyTerm().getLabel());
            }

            sdto.setSearchParameter(query);


            if (model.getSample() != null && model.getSample().getMolecularCharacterizations() != null) {
                Set<String> markerSet = new HashSet<>();

                for (MolecularCharacterization mc : model.getSample().getMolecularCharacterizations()) {
                    for (MarkerAssociation ma : mc.getMarkerAssociations()) {
                        markerSet.add(ma.getMarker().getName());
                    }
                }
                sdto.setCancerGenomics(new ArrayList<>(markerSet));

            }

            results.add(sdto);

        }

        return results;


    }


    public DetailsDTO searchForModel(String dataSource, String modelId, int page, int size,String technology,String passage,String searchFilter) {


        Sample sample = sampleRepository.findByDataSourceAndPdxId(dataSource,modelId);
        Patient patient = patientRepository.findByDataSourceAndModelId(dataSource,modelId);
        List<PatientSnapshot> ps = patientSnapshotRepository.findByDataSourceAndModelId(dataSource,modelId);
        ModelCreation pdx = modelCreationRepository.findByDataSourceAndSourcePdxId(dataSource, modelId);

        QualityAssurance qa = pdx.getQualityAssurance();

        int skip = page * size;
        int totalRecords = 0;
        Set<Specimen> specimens = new HashSet<>();


        totalRecords = specimenRepository.countBySearchParameterAndPlatform(dataSource,modelId,technology,passage,searchFilter);

        specimens = specimenRepository.findSpecimenBySourcePdxIdAndPlatform(dataSource,modelId,technology,passage,searchFilter,skip,size);


        DetailsDTO dto = new DetailsDTO();

                        /*
                        this.modelId = "";
                        this.externalId = "";
                        this.dataSource = "";
                        this.patientId = "";
                        this.gender = "";
                        this.age = "";
                        this.race = "";
                        this.ethnicity = "";
                        this.diagnosis = "";
                        this.tumorType = "";
                        this.classification = "";
                        this.originTissue = "";
                        this.sampleSite = "";Å

                        this.sampleType = "";
                        this.strain = "";
                        this.mouseSex = "";
                        this.engraftmentSite = "";
                         */

        Set< Set<MarkerAssociation> > markerAssociatonSet = new HashSet<>();
        List<Specimen> specimenList = new ArrayList<>();
        Set<MolecularCharacterization>  molecularCharacterizations = new HashSet<>();
        Set<Platform>  platforms = new HashSet<>();

        if (qa != null && qa.getValidationTechniques() != null) {
            dto.setTechnology(qa.getValidationTechniques().getTechnique());
        }

        if (qa != null && qa.getDescription() != null) dto.setDescription(qa.getDescription());
        if (qa != null && qa.getPassages() != null) dto.setPassages(qa.getPassages());

        if (specimens != null) {

            try {
                double dSize = size;
                dto.setTotalPages((int) Math.ceil(totalRecords/dSize) );
                dto.setVariationDataCount(totalRecords);
            }catch (Exception e){ }
        }

        // "NOD SCID GAMA"=>"P1, P2"
        Map<String, String> hostStrainMap = new HashMap<>();

        for (Specimen specimen : specimens) {
            try {
                specimenList.add(specimen);
                molecularCharacterizations.addAll(specimen.getSample().getMolecularCharacterizations());

                for (MolecularCharacterization dMolkar : specimen.getSample().getMolecularCharacterizations()) {
                    markerAssociatonSet.add(dMolkar.getMarkerAssociations());
                    platforms.add(dMolkar.getPlatform());
                }

            }catch (Exception e){ }
        }

        dto.setMarkerAssociations(markerAssociatonSet);
        dto.setMolecularCharacterizations(molecularCharacterizations);
        dto.setPlatforms(platforms);



        try {
            dto.setSpecimens(specimenList);
        }catch (Exception e){ }


        if (sample != null && sample.getSourceSampleId() != null) {
            dto.setExternalId(sample.getSourceSampleId());
        }

        if (sample != null && sample.getDataSource() != null) {
            dto.setDataSource(sample.getDataSource());
        }

        if (patient != null && patient.getExternalId() != null) {
            dto.setPatientId(patient.getExternalId());
        }

        if (patient != null && patient.getSex() != null) {
            dto.setGender(patient.getSex());
        }

        if (patient != null && patient.getExternalDataSource() != null) {
            dto.setContacts(patient.getExternalDataSource().getContact());
            dto.setExternalDataSourceDesc(patient.getExternalDataSource().getDescription());
        }

        if (ps != null) {
            for (PatientSnapshot patientSnapshots : ps) {
                if (patientSnapshots != null && patientSnapshots.getAge() != null) {
                    dto.setAge(patientSnapshots.getAge());
                }
            }

        }

        if (patient != null && patient.getRace() != null) {
            dto.setRace(patient.getRace());
        }

        if (patient != null && patient.getEthnicity() != null) {
            dto.setEthnicity(patient.getEthnicity());
        }

        if (sample != null && sample.getDiagnosis() != null) {
            dto.setDiagnosis(sample.getDiagnosis());
        }

        if (sample != null && sample.getType() != null) {
            dto.setTumorType(sample.getType().getName());
        }

        if (sample != null && sample.getClassification() != null) {
            dto.setClassification(sample.getClassification());
        }

        if (sample != null && sample.getOriginTissue() != null) {
            dto.setOriginTissue(sample.getOriginTissue().getName());
        }
        if (sample != null && sample.getSampleSite() != null) {
            dto.setSampleSite(sample.getSampleSite().getName());
        }

        if (sample != null && sample.getSampleToOntologyRelationShip() != null) {
            dto.setMappedOntology(sample.getSampleToOntologyRelationShip().getOntologyTerm().getLabel());
        }

        /*
        if (pdx != null && pdx.getImplantationType() != null) {
            dto.setSampleType(pdx.getImplantationType().getName());
        }

        if (pdx != null && pdx.getHostStrain() != null) {
            dto.setStrain(pdx.getHostStrain().getName());
        }

        if (pdx != null && pdx.getImplantationSite() != null) {
            dto.setEngraftmentSite(pdx.getImplantationSite().getName());
        }
        */
        if (pdx != null && pdx.getSourcePdxId() != null) {
            dto.setModelId(pdx.getSourcePdxId());
        }

        if(pdx!= null && pdx.getSpecimens() != null){

            Set<Specimen> sp = pdx.getSpecimens();

            for(Specimen s:sp){

                if(s.getHostStrain() != null && s.getPassage() != null){

                    String hostStrain = s.getHostStrain().getName();
                    String p = s.getPassage();

                    if(hostStrainMap.containsKey(hostStrain)){
                        String composedPassage = hostStrainMap.get(hostStrain);
                        composedPassage+=", P"+p;
                        hostStrainMap.put(hostStrain, composedPassage);

                    }
                    else{
                        hostStrainMap.put(hostStrain, "P"+p);
                    }


                    //Set implantation site and type
                    if(s.getImplantationSite() != null){
                        dto.setEngraftmentSite(s.getImplantationSite().getName());
                    }
                    else{

                        dto.setEngraftmentSite("Not Specified");
                    }

                    if(s.getImplantationType() != null){
                        dto.setSampleType(s.getImplantationType().getName());
                    }
                    else{

                        dto.setSampleType("Not Specified");
                    }



                }

            }


        }

        String composedStrain = "";

        if(hostStrainMap.size() > 1){

            for (Map.Entry<String, String> entry : hostStrainMap.entrySet()) {
                String strain = entry.getKey();
                String passages = entry.getValue();

                composedStrain += strain+" ("+passages+" ); ";
            }
        }
        else if(hostStrainMap.size() == 1){

            for(String key:hostStrainMap.keySet()){
                composedStrain = key;
            }
        }
        else{
            composedStrain = "Not Specified";
        }

        dto.setStrain(composedStrain);


        if (sample != null && sample.getMolecularCharacterizations() != null) {
            List<String> markerList = new ArrayList<>();

            for (MolecularCharacterization mc : sample.getMolecularCharacterizations()) {
                for (MarkerAssociation ma : mc.getMarkerAssociations()) {

                   /* if (ma.getDescription().equals("None")) {
                        markerList.add("None");
                    } else {
                        markerList.add(ma.getMarker().getName() + " status: " + ma.getDescription());
                    }*/

                }
            }
            Collections.sort(markerList);
            dto.setCancerGenomics(markerList);

        }

        if (sample != null && sample.getDataSource().equals("JAX")) {
            dto.setExternalUrl(JAX_URL+pdx.getSourcePdxId());
            dto.setExternalUrlText(JAX_URL_TEXT);
        } else if (sample != null && sample.getDataSource().equals("IRCC")) {
            dto.setExternalUrl(IRCC_URL + dto.getExternalId());
            dto.setExternalUrlText(IRCC_URL_TEXT);
        } else if(sample != null && sample.getDataSource().equals(HCI_DS)) {
            dto.setExternalUrl(HCI_URL);
            dto.setExternalUrlText(HCI_DS);
        } else if(sample != null && sample.getDataSource().equals(MDA_DS)) {
            dto.setExternalUrl(MDA_URL);
            dto.setExternalUrlText(MDA_DS);
        } else if(sample != null && sample.getDataSource().equals(WUSTL_DS)) {
            dto.setExternalUrl(WUSTL_URL);
            dto.setExternalUrlText(WUSTL_DS);
        } else if(sample != null && sample.getDataSource().equals(WISTAR_DS)) {
            dto.setExternalUrl(WISTAR_URL);
            dto.setExternalUrlText(WISTAR_DS);
        }
        else if(sample != null && sample.getDataSource().equals("PDMR")) {
            dto.setExternalUrl(PDMR_URL);
            dto.setExternalUrlText(PDMR_URL_TEXT);
        }
        else{
            dto.setExternalUrl("#");
            dto.setExternalUrlText("Unknown source");
        }


        return dto;
    }





    public Map<String, Set<String>> findModelPlatformAndPassages(String dataSource, String modelId,String passage){

        /**
         * Used to store a technology String with their respective List of PDX Passages
         */
        Map<String, Set<String>> platformMap = new HashMap<>();

        /**
         * Retrieve all the technologies for that mouse model
         */
        List<Platform> platforms = platformRepository.findModelPlatformByModelId(dataSource,modelId);

        /**
         * For each of the technologies retrieve the list of PDX passages using the specimen repository
         */
        for (Platform platform : platforms) {

            List<Specimen> specimens = specimenRepository.findSpecimenBySourcePdxIdAndPlatform2(dataSource,modelId,platform.getName());

            Set<String> passagesList = new HashSet<>();
            for (Specimen specimen : specimens)
            {
                passagesList.add(specimen.getPassage()+"");
            }

            platformMap.put(platform.getName(), passagesList);
        }

        return platformMap;
    }



    public Map findPatientPlatforms(String dataSource, String modelId){

        Map<String, String> platformMap = new HashMap<>();

        List<MolecularCharacterization> molecularCharacterizations = molecularCharacterizationRepository.findPatientPlatformByModelId(dataSource,modelId);

        for (MolecularCharacterization mc : molecularCharacterizations) {

            if(mc.getPlatform() != null){
                platformMap.put(mc.getPlatform().getName(), mc.getPlatform().getName());
            }

        }

        return platformMap;
    }


    public VariationDataDTO patientVariationDataByPlatform(String dataSource, String modelId, String technology,
                                                           String searchParam, int draw, String sortColumn, String sortDir, int start, int size) {

        //int recordsTotal = patientRepository.countByBySourcePdxIdAndPlatform(dataSource,modelId,technology,"");

        int recordsTotal = modelCreationRepository.variationCountByDataSourceAndPdxIdAndPlatform(dataSource,modelId,technology,"");

        int recordsFiltered = recordsTotal;

        if (!searchParam.isEmpty()) {
            recordsFiltered = modelCreationRepository.variationCountByDataSourceAndPdxIdAndPlatform(dataSource,modelId,technology,searchParam);
        }

        /**
         * Retrieve the Records based on search parameter
         */
        ModelCreation model = modelCreationRepository.findVariationBySourcePdxIdAndPlatform(dataSource,modelId,technology,searchParam,start,size);
        VariationDataDTO variationDataDTO = new VariationDataDTO();
        List<String[]> variationData = new ArrayList();

        if (model != null && model.getSample() != null ) {

            variationData.addAll(buildUpDTO(model.getSample(),draw,recordsTotal,recordsFiltered));
        }

        variationDataDTO.setDraw(draw);
        variationDataDTO.setRecordsTotal(recordsTotal);
        variationDataDTO.setRecordsFiltered(recordsFiltered);
        variationDataDTO.setData(variationData);

        return variationDataDTO;

    }


    public VariationDataDTO variationDataByPlatform(String dataSource, String modelId, String technology,String passage, int start, int size,
                                                    String searchParam, int draw, String sortColumn, String sortDir) {
        /**
         * 1st count all the records and set Total Records & Initialize Filtered Record as Total record
         */
        //int recordsTotal = specimenRepository.countBySearchParameterAndPlatform(dataSource,modelId,technology,passage,"");
        int recordsTotal = specimenRepository.countByPlatform(dataSource,modelId,technology,passage);

        int recordsFiltered = recordsTotal;

        /**
         * If search Parameter is not empty: Count and Reset the value of filtered records based on search Parameter
         */
        if (!searchParam.isEmpty()) {
            recordsFiltered = specimenRepository.countBySearchParameterAndPlatform(dataSource,modelId,technology,passage,searchParam);
        }

        /**
         * Retrieve the Records based on search parameter
         */
        Set<Specimen> specimens = specimenRepository.findSpecimenBySourcePdxIdAndPlatform(dataSource,modelId,technology,passage,searchParam,start,size);
        VariationDataDTO variationDataDTO = new VariationDataDTO();
        List<String[]> variationData = new ArrayList();

        if (specimens != null) {
            for (Specimen specimen : specimens) {
                variationData.addAll( buildUpDTO(specimen.getSample(),draw,recordsTotal,recordsFiltered) );
            }
        }

        variationDataDTO.setDraw(draw);
        variationDataDTO.setRecordsTotal(recordsTotal);
        variationDataDTO.setRecordsFiltered(recordsFiltered);
        variationDataDTO.setData(variationData);

        return variationDataDTO;

    }


    public List<String> getModelsOriginatedFromSamePatient(String dataSource, String modelId){

        return patientRepository.getModelsOriginatedFromSamePatientByDataSourceAndModelId(dataSource, modelId);
    }


    public List<DrugSummaryDTO> getDrugSummary(String dataSource, String modelId){

        TreatmentSummary ts = treatmentSummaryRepository.findByDataSourceAndModelId(dataSource, modelId);

        List<DrugSummaryDTO> results = new ArrayList<>();

        if(ts != null && ts.getTreatmentProtocols() != null){

            for(TreatmentProtocol tp : ts.getTreatmentProtocols()){

                DrugSummaryDTO dto = new DrugSummaryDTO();
                dto.setDrugName(tp.getDrug());
                dto.setDose(tp.getDose());

                if(tp.getResponse() != null && tp.getResponse().getDescription() != null){
                    dto.setResponse(tp.getResponse().getDescription());
                }
                else{
                    dto.setResponse("");
                }

                results.add(dto);
            }
        }

        return results;
    }

    public List<String[]> buildUpDTO(Sample sample,int draw,int recordsTotal,int recordsFiltered){

        List<String[]> variationData = new ArrayList();

        /**
         * Generate an equivalent 2-D array type of the Retrieved result Set, the Front end table must be a 2D JSON Array
         */
        try {

            for (MolecularCharacterization dMolChar : sample.getMolecularCharacterizations()) {

                List<MarkerAssociation> markerAssociation = new ArrayList();// = dMolChar.getMarkerAssociations();
                markerAssociation.addAll(dMolChar.getMarkerAssociations());

                for (MarkerAssociation markerAssoc : markerAssociation) {

                    String[] markerAssocArray = new String[12];
                    markerAssocArray[0] = sample.getSourceSampleId();
                    //markerAssocArray[1] = dMolChar.getPlatform().getName();
                    markerAssocArray[1] = markerAssoc.getChromosome();
                    markerAssocArray[2] = markerAssoc.getSeqPosition();
                    markerAssocArray[3] = markerAssoc.getRefAllele();
                    markerAssocArray[4] = markerAssoc.getAltAllele();
                    markerAssocArray[5] = markerAssoc.getConsequence();
                    markerAssocArray[6] = markerAssoc.getMarker().getSymbol();
                    markerAssocArray[7] = markerAssoc.getAminoAcidChange();
                    markerAssocArray[8] = markerAssoc.getReadDepth();
                    markerAssocArray[9] = markerAssoc.getAlleleFrequency();
                    markerAssocArray[10 ] = markerAssoc.getRsVariants();

                    variationData.add(markerAssocArray);
                }
            }
        }catch (Exception e) { }

        return variationData;
    }







}
