package paddletest2.com;
import hudson.EnvVars;
import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.util.FormValidation;
import hudson.model.AbstractProject;
import hudson.model.ParametersAction;
import hudson.model.Failure;
import hudson.model.Run;
import hudson.model.PasswordParameterValue;
import hudson.model.StringParameterValue;
import hudson.model.TextParameterValue;
import hudson.model.StringParameterValue;
import hudson.model.ParameterValue;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link HelloWorldBuilder} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #name})
 * to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link #perform} method will be invoked. 
 *
 * @author Kohsuke Kawaguchi
 */
public class HelloWorldBuilder extends Builder implements SimpleBuildStep {

    private final String name;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public HelloWorldBuilder(String name) {
        this.name = name;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getName() {
        return name;
    }

    @Override
    public void perform(Run<?,?> build, FilePath workspace, Launcher launcher, TaskListener listener) {
        // This is where you 'build' the project.
        // Since this is a dummy, we just say 'hello world' and call that a build.

        // This also shows how you can consult the global configuration of the builder
		System.setProperty("accessUrl", "Java Runtim");
		
		
		listener.getLogger().println("value start");
    	ParametersAction parameters = build.getAction(ParametersAction.class);
        if (parameters != null) {
            listener.getLogger().println("Parameters is there in the build");
            List<ParameterValue> pValues = parameters.getParameters();
    		
               for(ParameterValue pv : pValues) {
               	if (pv instanceof StringParameterValue) {
               		listener.getLogger().println("value in p+"+pv.getValue());
               		
               	}
               }
        }
        else
        {
        	listener.getLogger().println("Parameters is not build");
        	//listener.fatalError("hai fatal error"); 
        }
		
		
		
		
		
		
		   ParametersAction parameters2 = build.getAction(ParametersAction.class);
		Map<String, String> parameterMap = new HashMap<String, String>();
		
		Map<String, String> envMap = System.getenv();
		for (String envKey : envMap.keySet()) {
			parameterMap.put(envKey, envMap.get(envKey));
		}
		
		if (null != parameters2) {
			for (ParameterValue parameterValue : parameters2.getParameters()) {
				if (parameterValue instanceof TextParameterValue) {
					parameterMap.put(parameterValue.getName(), ((TextParameterValue) parameterValue).value);
					listener.getLogger().println("Parameters is build"+parameterValue.getName());
				}
				if (parameterValue instanceof StringParameterValue) {
					parameterMap.put(parameterValue.getName(), ((StringParameterValue) parameterValue).value);
					listener.getLogger().println("Parameters is build"+parameterValue.getName());
				}
				if (parameterValue instanceof PasswordParameterValue) {
					parameterMap.put(parameterValue.getName(), ((PasswordParameterValue) parameterValue).getValue().getPlainText());
					listener.getLogger().println("Parameters is build"+parameterValue.getName());
				}
			}
		}
		else
			listener.getLogger().println("Parameters is not build");
		
		

        listener.getLogger().println("value end**");
		
		
		
		EnvVars envVars = new EnvVars();
		try {
			envVars = build.getEnvironment(listener);
			envVars.put("accessUrl7", "access");
			listener.getLogger().println("env vars"+envVars.get("JOB_NAME")+"build_tag"+envVars.get("BUILD_TAG"));
			listener.getLogger().println("access in var"+envVars.get("accessUrl7"));
			//add parameterized build
			 List<ParameterValue> params = new ArrayList<ParameterValue>();
			    params.add(new StringParameterValue("accessUrl", "http:////:8090"));
			    //params.add(new StringParameterValue(name2, value2));
			    /*List<ParameterValue> params = new ArrayList<ParameterValue>();
    			params.add(new StringParameterValue(name1, value1));
    			params.add(new StringParameterValue(name2, value2));
    			build.addAction(new ParametersAction(params));*/
			    build.addAction(new ParametersAction(params));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		envVars.put("accessUrl7", "access");
		 ProcessBuilder pb= new ProcessBuilder();

		 
		   // get the environment of the process
		   Map<String, String> env = pb.environment();
		   env.put("accessUrl", "http://accessPort");
		   listener.getLogger().println("env"+env.get("accessUrl"));

		listener.getLogger().println("property"+System.getProperty("accessUrl"));
        if (getDescriptor().getUseFrench())
		{
            listener.getLogger().println("Bonjour, "+name+"!");
			  //throw new Failure(hudson.model.Messages.Hudson_NotAPlugin("hai error"));
			           
		}
        else
            listener.getLogger().println("Hello, "+name+"!");
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    /**
     * Descriptor for {@link HelloWorldBuilder}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See <tt>src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         *
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        private boolean useFrench;

        /**
         * In order to load the persisted global configuration, you have to 
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        /**
         * Performs on-the-fly validation of the form field 'name'.
         *
         * @param value
         *      This parameter receives the value that the user has typed.
         * @return
         *      Indicates the outcome of the validation. This is sent to the browser.
         *      <p>
         *      Note that returning {@link FormValidation#error(String)} does not
         *      prevent the form from being saved. It just means that a message
         *      will be displayed to the user. 
         */
        public FormValidation doCheckName(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set a name");
            if (value.length() < 4)
                return FormValidation.warning("Isn't the name too short?");
            return FormValidation.ok();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Say hello world";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            useFrench = formData.getBoolean("useFrench");
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            save();
            return super.configure(req,formData);
        }

        /**
         * This method returns true if the global configuration says we should speak French.
         *
         * The method name is bit awkward because global.jelly calls this method to determine
         * the initial state of the checkbox by the naming convention.
         */
        public boolean getUseFrench() {
            return useFrench;
        }
    }
}

