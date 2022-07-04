import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.SummaryEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SampleClient {


    public static void main(String[] theArgs) throws IOException {

        // Create a FHIR client
        FhirContext fhirContext = FhirContext.forR4();
        IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4/");
        client.registerInterceptor(new LoggingInterceptor(false));

        final String lastNames = "C:\\Users\\Nikhil Jadav\\Desktop\\Dev\\Assignment\\playground-basic\\src\\main\\resources\\lastNames.txt";

        // Search for Patient resources
        Bundle response = client
                .search()
                .forResource(Patient.class)
                .where(Patient.FAMILY.matches().values("SMITH"))
                .sort()
                .ascending(Patient.NAME)
                .returnBundle(Bundle.class)
                .elementsSubset("name" , "birthDate")
                .execute();

        //List of Patient
        List<Patient> patient = patientInfo(response);

        System.out.printf("%10s %10s %s \n", "firstName", "lastName", "birthDate");
        getPatientDetail(patient);

        // Sort by firstName
        patient.sort(new SortByPatientFirstName());
        System.out.println("\n------>>>>Sorted Patient's FirstName <<<<<<------------");
        System.out.printf("%10s  %10s %s \n", "firstName", "lastName", "birthDate");

        //print patient details
        getPatientDetail(patient);

    }


// Get patients from bundle & retuns them as List
// Return List from Bundle Patients
    private static List<Patient> patientInfo(Bundle bundle) {
        List<Patient> patient = new ArrayList<>();
        for (Bundle.BundleEntryComponent eachBundle : bundle.getEntry()) {
            if (eachBundle.getResource() instanceof Patient) {
                patient.add((Patient) eachBundle.getResource());
            }
        }
        // Return the list of Patient detials
        return patient;
    }

//Get & Print Pationt's firstName & birthDate
    private static void getPatientDetail(List<Patient> data) {
        for (Patient patient : data) {
            HumanName humanName = patient.getName().get(0);
            Date birthDate = patient.getBirthDate();
            System.out.printf("%10s  %-10s %s \n", humanName.getGiven().get(0).toString(), humanName.getFamily(), (birthDate != null) ? new SimpleDateFormat("yyyy.MMMM.dd").format(birthDate) : "n/a");
        }
    }
}

//Using Comparator - sort patient's first Name
class SortByPatientFirstName implements Comparator<Patient> {
    public int compare(Patient patient1, Patient patient2) {
        String patientName1 = patient1.getName().get(0).getGiven().get(0).toString();
        String patientName2 = patient2.getName().get(0).getGiven().get(0).toString();
        return patientName1.compareToIgnoreCase(patientName2);
    }
}
