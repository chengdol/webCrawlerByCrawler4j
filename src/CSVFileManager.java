import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;

// this class is singleton for each file object
public class CSVFileManager
{
	private static final PrintWriter attemptFile = createAttemptFile();
	private static final PrintWriter visitFile = createVisitFile();
	private static final PrintWriter allURLFile = createAllURLFile();
	
	// initializer with exception handler for static field
	private static PrintWriter createAttemptFile()
	{
		PrintWriter ret = null;
		try
		{
			ret =  new PrintWriter(new FileOutputStream(new File("fetch_foxnews.csv"), true));
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	
	private static PrintWriter createVisitFile()
	{
		PrintWriter ret = null;
		try
		{
			ret =  new PrintWriter(new FileOutputStream(new File("visit_foxnews.csv"), true));
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	
	private static PrintWriter createAllURLFile()
	{
		PrintWriter ret = null;
		try
		{
			ret =  new PrintWriter(new FileOutputStream(new File("urls_foxnews.csv"), true));
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	
	// getter
	public static PrintWriter getAttemptFile()
	{
		return attemptFile;
	}
	
	public static PrintWriter getVisitFile()
	{
		return visitFile;
	}
	
	public static PrintWriter getAllURLFile()
	{
		return allURLFile;
	}
	
	// writer
	public static void writeAttemptFile(List<String> data)
	{
		synchronized(attemptFile)
		{
			for(String row : data)
			{
				attemptFile.write(row);
			}	
			// don't forget
			attemptFile.flush();
		}
		
	}
	
	public static void writeVisitFile(List<String> data)
	{
		synchronized(visitFile)
		{
			for(String row : data)
			{
				visitFile.write(row);
			}
			visitFile.flush();
		}
	}
	
	public static void writeAllURLFile(List<String> data)
	{
		synchronized(allURLFile)
		{
			for(String row : data)
			{
				allURLFile.write(row);
			}
			allURLFile.flush();
		}
	}
	
	// close
	public static void closeFiles()
	{
		attemptFile.close();
		visitFile.close();
		allURLFile.close();
	}
	
	public static void main(String[] args) throws FileNotFoundException
	{
		// test
		PrintWriter f1 = CSVFileManager.getAttemptFile();
		PrintWriter f2 = CSVFileManager.getVisitFile();
		PrintWriter f3 = CSVFileManager.getAllURLFile();
		
		StringBuilder sb = new StringBuilder();
		sb.append("id").append(",").append("name").append("\n");
		sb.append("123456678").append(",").append("chengdol").append("\n");
		
		f1.write(sb.toString());
		f2.write(sb.toString());
		f3.write(sb.toString());
		
		CSVFileManager.closeFiles();
	}
}
