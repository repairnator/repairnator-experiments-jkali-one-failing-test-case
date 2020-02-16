package name.mjw.jquante.common.resource;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * 
 * @author mw529
 * @see <a href="http://www.vogella.com/tutorials/JAXB/article.html">Example
 *      JAXB tutorial</a>
 */
public class NWChemBasisSetXMLWriter {

	public static void main(String[] args) throws JAXBException {


		String fileName = "/usr/local/shared/ubuntu-16.04/x86_64/nwchem/6.6/data/libraries/sto-3g";
		
		NWChemBasisSetFile nWChemBasisSetFile = new NWChemBasisSetFile();

		nWChemBasisSetFile.read(fileName);

		// create JAXB context and instantiate marshaller
		JAXBContext context = null;
		context = JAXBContext.newInstance(NWChemBasisSetFile.class);

		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		try {
			OutputStream os = new FileOutputStream(
					"basis_" + nWChemBasisSetFile.getBasisSetName() + ".xml");
			m.marshal(nWChemBasisSetFile, os);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

}
