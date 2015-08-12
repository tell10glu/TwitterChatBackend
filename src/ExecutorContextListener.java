

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class ExecutorContextListener
 *
 */
@WebListener
public class ExecutorContextListener implements ServletContextListener {

	public static final String EXECUTOR_SERVICE = "EXECUTOR_SERVICE";
	private ExecutorService executorService;
    /**
     * Default constructor. 
     */
    public ExecutorContextListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0)  { 
         executorService = Executors.newCachedThreadPool();
         ServletContext context = arg0.getServletContext();
         context.setAttribute(EXECUTOR_SERVICE, executorService);
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
    }
	
}
