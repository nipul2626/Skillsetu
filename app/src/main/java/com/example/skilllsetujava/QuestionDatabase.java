package com.example.skilllsetujava;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Comprehensive Question Database for all job roles and interview types
 * Used as fallback when AI generation fails
 */
public class QuestionDatabase {

    private static final Random random = new Random();

    // Question bank structure: Role -> InterviewType -> Questions[]
    private static final Map<String, Map<String, String[]>> questionBank = new HashMap<>();

    static {
        initializeQuestions();
    }

    private static void initializeQuestions() {
        // SOFTWARE DEVELOPER
        Map<String, String[]> softwareDev = new HashMap<>();
        softwareDev.put("HR Interview", new String[]{
                "Tell me about yourself and your journey into software development.",
                "Why are you interested in working as a software developer at our company?",
                "Describe a challenging project you worked on and how you overcame obstacles.",
                "How do you stay updated with the latest technologies and programming trends?",
                "Tell me about a time when you had to work in a team. What was your role?",
                "Where do you see yourself in 5 years as a software developer?",
                "What are your greatest strengths and weaknesses as a developer?",
                "How do you handle tight deadlines and pressure in software projects?"
        });
        softwareDev.put("Technical", new String[]{
                "Explain the difference between compile-time and runtime errors.",
                "What is the difference between stack and heap memory?",
                "Explain object-oriented programming concepts with real-world examples.",
                "What are design patterns? Explain any two patterns you've used.",
                "How do you optimize the performance of an application?",
                "Explain the concept of version control. How do you use Git?",
                "What is the difference between synchronous and asynchronous programming?",
                "Describe the software development lifecycle you follow.",
                "What is debugging? What tools and techniques do you use?",
                "Explain REST API and how you would design one.",
                "What is test-driven development? Have you practiced it?",
                "How would you handle database optimization in a large application?"
        });
        softwareDev.put("Aptitude", new String[]{
                "If a program takes 2 seconds to process 100 records, how long will it take for 1000 records?",
                "You have 8 modules to test. If each test takes 15 minutes, how long total?",
                "A code review takes 30 minutes per 100 lines. How long for 850 lines?",
                "If 5 developers complete a project in 10 days, how many days for 2 developers?",
                "Find the next number in the sequence: 2, 6, 12, 20, 30, ?",
                "A bug occurs once every 1000 executions. How many bugs in 25,000 executions?",
                "If compilation takes 3 minutes for 500 files, estimate time for 2000 files.",
                "What percentage is 45 out of 180?",
                "Calculate: If CPU usage is 75% with 4 processes, what's usage per process?",
                "A server handles 100 requests/second. How many in 1 hour?",
                "If memory usage grows by 10% each day, what's usage after 3 days starting at 100MB?",
                "Find pattern: 1, 1, 2, 3, 5, 8, 13, ?",
                "If a sorting algorithm complexity is O(n²), time for n=1000?",
                "Calculate throughput: 500MB in 50 seconds equals __ MB/s",
                "A system has 99.9% uptime. How many minutes downtime per month?"
        });
        softwareDev.put("Mixed", new String[]{
                "Describe your experience with Agile methodology. How do you apply it?",
                "Explain a technical challenge you faced and how you solved it.",
                "How do you prioritize tasks when working on multiple features?",
                "What's your approach to learning a new programming language or framework?",
                "If you had to explain APIs to a non-technical person, how would you do it?",
                "Describe your code review process and what you look for.",
                "How do you balance code quality with meeting project deadlines?",
                "What metrics would you use to measure code quality?",
                "Tell me about a time you had to debug a critical production issue.",
                "How would you approach designing a scalable system from scratch?"
        });
        questionBank.put("Software Developer", softwareDev);

        // ANDROID DEVELOPER
        Map<String, String[]> androidDev = new HashMap<>();
        androidDev.put("HR Interview", new String[]{
                "Why did you choose Android development as your career path?",
                "Tell me about your most successful Android app project.",
                "How do you handle user feedback and app store reviews?",
                "What motivates you to keep learning in mobile development?",
                "Describe your experience working with cross-functional teams.",
                "How do you stay updated with Android SDK updates and best practices?",
                "What's your approach to work-life balance in a demanding project?",
                "Tell me about a time you had to learn a new Android technology quickly."
        });
        androidDev.put("Technical", new String[]{
                "Explain the Android activity lifecycle with a diagram.",
                "What is the difference between Activity and Fragment?",
                "How does Android memory management work? Explain garbage collection.",
                "What are the different types of Android layouts? When would you use each?",
                "Explain the concept of Intent in Android. Types and uses.",
                "What is the difference between Service, IntentService, and JobScheduler?",
                "How do you handle background tasks in Android?",
                "Explain the MVVM architecture pattern in Android.",
                "What is dependency injection? Have you used Dagger or Hilt?",
                "How do you optimize Android app performance and reduce APK size?",
                "Explain RecyclerView and how it differs from ListView.",
                "What is Jetpack Compose? How does it differ from XML layouts?"
        });
        androidDev.put("Aptitude", new String[]{
                "An app has 5000 users. If 15% uninstall monthly, how many after 2 months?",
                "Calculate: App loads in 3.5 seconds. Target is 2 seconds. What % improvement needed?",
                "If an APK size is 45MB and you reduce it by 30%, what's the new size?",
                "A battery drains 15% per hour. How many hours until 20% remaining from 100%?",
                "App crashes occur in 2% of sessions. How many crashes in 10,000 sessions?",
                "If 60% of users are on Android 11+, how many devices out of 8000 total?",
                "Calculate: Loading time increases by 0.5s per 100 images. Time for 450 images?",
                "An API call takes 800ms. Target is under 500ms. What % reduction needed?",
                "Find pattern: 1, 4, 9, 16, 25, ?",
                "If app downloads grew from 1000 to 2500, what's the percentage increase?",
                "Memory usage: 150MB initial, grows 20% per feature. Usage after 3 features?",
                "Calculate: 3 developers complete 9 screens in 6 days. Time for 15 screens?",
                "If push notification open rate is 8%, opens from 5000 notifications?",
                "An ad generates $0.05 per click. Revenue from 2400 clicks?",
                "Calculate: App rating is 4.2 from 500 reviews. Need 4.5. What's required?"
        });
        androidDev.put("Mixed", new String[]{
                "How would you architect a large-scale Android application?",
                "Explain your testing strategy for Android apps (Unit, UI, Integration).",
                "How do you handle app security and protect sensitive user data?",
                "Describe your approach to supporting multiple screen sizes and densities.",
                "What's your process for publishing an app to Google Play Store?",
                "How would you implement offline-first architecture in an Android app?",
                "Explain how you would optimize an app for battery efficiency.",
                "Describe a challenging Android bug you fixed and your approach.",
                "How do you ensure your app follows Material Design guidelines?",
                "What's your strategy for handling app localization and internationalization?"
        });
        questionBank.put("Android Developer", androidDev);

        // WEB DEVELOPER
        Map<String, String[]> webDev = new HashMap<>();
        webDev.put("HR Interview", new String[]{
                "What inspired you to become a web developer?",
                "Tell me about your favorite web project and why it stands out.",
                "How do you approach learning new web technologies?",
                "Describe your experience with client communication and requirement gathering.",
                "How do you handle browser compatibility issues?",
                "What's your process for staying current with web development trends?",
                "Tell me about a time you had to meet a challenging deadline.",
                "How do you balance creativity with functionality in web design?"
        });
        webDev.put("Technical", new String[]{
                "Explain the difference between HTML, CSS, and JavaScript roles.",
                "What is the DOM? How do you manipulate it?",
                "Explain the box model in CSS.",
                "What are responsive web design principles?",
                "Explain JavaScript closures with an example.",
                "What is the difference between let, const, and var?",
                "How does asynchronous JavaScript work? Explain promises and async/await.",
                "What is the difference between SQL and NoSQL databases?",
                "Explain RESTful API design principles.",
                "What are HTTP status codes? Explain 200, 404, 500.",
                "How do you optimize website loading speed?",
                "Explain Cross-Origin Resource Sharing (CORS)."
        });
        webDev.put("Aptitude", new String[]{
                "A website loads in 8 seconds. After optimization, it loads in 5 seconds. What % faster?",
                "If a page has 50 images averaging 200KB each, what's total size?",
                "Calculate: Server handles 500 requests/min. How many in 1 hour?",
                "If 70% of visitors use mobile, how many mobile users from 2000 visitors?",
                "A website's bounce rate is 45%. How many stay from 800 visitors?",
                "Calculate: Loading time reduced from 6s to 4s. What's the percentage decrease?",
                "If conversion rate is 3%, how many conversions from 5000 visitors?",
                "A server uptime is 99.5%. Downtime minutes in a 30-day month?",
                "Calculate: 15 web pages, 3 hours each. Total time with 2 developers?",
                "If CSS file size is 450KB and you compress 40%, what's new size?",
                "Pattern: 2, 4, 8, 16, 32, ?",
                "Calculate: API latency is 300ms. Target is 200ms. What % reduction?",
                "If website traffic grows 25% monthly, traffic after 2 months from 1000?",
                "A form has 10 fields. 20% are required. How many required fields?",
                "Calculate bandwidth: 50GB data transferred in 100 hours equals __ GB/hour?"
        });
        webDev.put("Mixed", new String[]{
                "How would you design a scalable web application architecture?",
                "Explain your approach to web security (XSS, CSRF, SQL injection).",
                "How do you ensure web accessibility (WCAG compliance)?",
                "Describe your deployment and CI/CD process for web applications.",
                "What's your strategy for SEO optimization?",
                "How would you implement authentication and authorization in a web app?",
                "Explain your approach to progressive web apps (PWA).",
                "How do you handle state management in modern web applications?",
                "Describe your testing strategy for web applications.",
                "What tools and techniques do you use for debugging web applications?"
        });
        questionBank.put("Web Developer", webDev);

        // CYBER SECURITY
        Map<String, String[]> cyberSec = new HashMap<>();
        cyberSec.put("HR Interview", new String[]{
                "What motivated you to pursue a career in cybersecurity?",
                "Describe a security incident you handled and how you resolved it.",
                "How do you stay updated with the latest security threats and vulnerabilities?",
                "Tell me about your experience with security certifications.",
                "How do you communicate security risks to non-technical stakeholders?",
                "Describe a time when you had to work under pressure during a security breach.",
                "What ethical considerations do you keep in mind in cybersecurity?",
                "How do you balance security with user experience?"
        });
        cyberSec.put("Technical", new String[]{
                "Explain the difference between symmetric and asymmetric encryption.",
                "What is the CIA triad in information security?",
                "Describe common types of cyber attacks (DDoS, phishing, malware).",
                "How does SSL/TLS work? Explain the handshake process.",
                "What is penetration testing? What tools have you used?",
                "Explain SQL injection attacks and prevention methods.",
                "What is the difference between IDS and IPS?",
                "How do firewalls work? Types of firewalls?",
                "Explain authentication vs authorization with examples.",
                "What is two-factor authentication? How does it work?",
                "Describe the concept of defense in depth.",
                "How would you secure a REST API?"
        });
        cyberSec.put("Aptitude", new String[]{
                "If a password has 8 characters (26 letters), how many possible combinations?",
                "A system blocks 95% of attacks. How many attacks succeed from 1000?",
                "Calculate: Encryption takes 0.002s per KB. Time for 500KB?",
                "If security audits cost $5000 each and you conduct 6 yearly, total cost?",
                "A firewall blocks 98.5% of malicious traffic. How much gets through from 10,000 requests?",
                "Calculate: Virus scan takes 2 minutes per 1000 files. Time for 15,000 files?",
                "If a security patch reduces vulnerabilities by 60%, how many remain from 50?",
                "Calculate: Intrusion detection system has 99.2% accuracy. False positives from 5000 alerts?",
                "A password policy requires changes every 90 days. Changes in 2 years?",
                "If data breach costs $150 per record, cost for 2000 records?",
                "Pattern recognition: 128, 256, 512, 1024, ?",
                "Calculate: Network monitoring uses 5GB per day. Monthly usage?",
                "If encryption key length doubles from 128 to 256 bits, complexity increase?",
                "A system has 5 security layers, each 90% effective. Overall effectiveness?",
                "Calculate: 40% of emails are spam. Non-spam emails from 5000?"
        });
        cyberSec.put("Mixed", new String[]{
                "How would you design a comprehensive security architecture for an organization?",
                "Explain your incident response plan for a data breach.",
                "How do you conduct security risk assessments?",
                "Describe your approach to security awareness training for employees.",
                "What's your strategy for implementing zero-trust security?",
                "How would you secure a cloud-based infrastructure?",
                "Explain your approach to vulnerability management.",
                "How do you balance security requirements with business needs?",
                "Describe your experience with security compliance (GDPR, HIPAA, etc.).",
                "What tools and methodologies do you use for security testing?"
        });
        questionBank.put("Cyber Security", cyberSec);

        // DEVOPS ENGINEER
        Map<String, String[]> devOps = new HashMap<>();
        devOps.put("HR Interview", new String[]{
                "What attracted you to DevOps culture and practices?",
                "Describe your experience with automation and its impact.",
                "How do you handle on-call responsibilities and production incidents?",
                "Tell me about a time you improved deployment efficiency.",
                "How do you collaborate with development and operations teams?",
                "What's your approach to learning new DevOps tools and practices?",
                "Describe a challenging infrastructure problem you solved.",
                "How do you maintain work-life balance with 24/7 system responsibilities?"
        });
        devOps.put("Technical", new String[]{
                "Explain the DevOps lifecycle and its key principles.",
                "What is the difference between continuous integration and continuous deployment?",
                "How do containers differ from virtual machines?",
                "Explain Docker architecture and its components.",
                "What is Kubernetes? How does orchestration work?",
                "Describe Infrastructure as Code (IaC). What tools have you used?",
                "How do you implement CI/CD pipelines? Tools you prefer?",
                "Explain monitoring and logging best practices.",
                "What is configuration management? Ansible vs Chef vs Puppet?",
                "How do you handle secrets management in DevOps?",
                "Explain blue-green deployment and canary releases.",
                "What is GitOps? How does it work?"
        });
        devOps.put("Aptitude", new String[]{
                "A deployment takes 15 minutes. How much time saved with automation to 3 minutes?",
                "If server capacity is 1000 requests/s and usage is 60%, how many more requests possible?",
                "Calculate: 20 deployments per day, 10 minutes each. Daily time spent?",
                "If cloud costs are $8000/month and you optimize by 25%, new cost?",
                "A backup takes 2GB per day. Storage needed for 30 days?",
                "Calculate: System uptime 99.9%. Downtime minutes in a year?",
                "If 5 servers handle 5000 users, how many servers for 15,000 users?",
                "Calculate: Build time is 12 minutes, reduced by 40%. New time?",
                "Pattern: 64, 128, 256, 512, ?",
                "If scaling adds 20% capacity per instance, total from 3 instances at 100 each?",
                "Calculate: Monitoring generates 50MB logs/hour. Daily total?",
                "If automation saves 2 hours daily, hours saved monthly?",
                "Calculate: CPU usage 75% with 4 cores. Usage per core?",
                "A pipeline has 8 stages, average 5 minutes each. Total time?",
                "If disaster recovery time is 4 hours, target is 1 hour. What % reduction?"
        });
        devOps.put("Mixed", new String[]{
                "How would you design a highly available and scalable infrastructure?",
                "Explain your approach to implementing security in DevOps (DevSecOps).",
                "How do you handle database migrations in CI/CD pipelines?",
                "Describe your disaster recovery and backup strategy.",
                "What's your approach to capacity planning and auto-scaling?",
                "How do you implement monitoring and alerting for microservices?",
                "Explain your strategy for managing multiple environments (dev, staging, prod).",
                "How would you troubleshoot a production performance issue?",
                "Describe your experience with cloud platforms (AWS, Azure, GCP).",
                "What metrics do you track to measure DevOps success?"
        });
        questionBank.put("DevOps Engineer", devOps);

        // UI/UX DESIGNER
        Map<String, String[]> uiux = new HashMap<>();
        uiux.put("HR Interview", new String[]{
                "What inspired you to become a UI/UX designer?",
                "Tell me about your design process from concept to final product.",
                "How do you handle client feedback and design revisions?",
                "Describe a project where user research significantly changed your design.",
                "How do you stay updated with current design trends?",
                "Tell me about a time you had to defend your design decisions.",
                "How do you balance aesthetics with functionality?",
                "What's your approach to working with developers?"
        });
        uiux.put("Technical", new String[]{
                "Explain the difference between UI and UX design.",
                "What is user-centered design? How do you implement it?",
                "Describe your wireframing and prototyping process.",
                "What tools do you use for UI/UX design? (Figma, Sketch, Adobe XD)",
                "Explain design systems and their importance.",
                "What is responsive design? How do you approach it?",
                "Describe the principles of good visual hierarchy.",
                "What is A/B testing? How do you use it in design?",
                "Explain accessibility in design (WCAG guidelines).",
                "What are design patterns? Give examples.",
                "How do you conduct user research and usability testing?",
                "Explain the concept of information architecture."
        });
        uiux.put("Aptitude", new String[]{
                "A design sprint is 5 days. How many sprints in 30 days?",
                "If user satisfaction increased from 65% to 85%, what's the improvement?",
                "Calculate: 10 screens, 8 hours each, 2 designers. Total time?",
                "If conversion rate improved from 2% to 3.5%, what's percentage increase?",
                "A/B test: Version A has 45% success, Version B has 60%. Difference?",
                "Calculate: Redesign costs $15,000, increases revenue by $3,000/month. Break-even?",
                "If bounce rate decreased from 55% to 40%, what percentage decreased?",
                "Pattern: 360, 180, 90, 45, ?",
                "Calculate: 20 user interviews at 45 minutes each. Total hours?",
                "If 80% of users prefer Design A, how many from 500 users?",
                "Calculate: Loading time reduced from 7s to 4s. Percentage improvement?",
                "A button's click-through rate is 12%. Clicks from 5000 views?",
                "If design iterations take 3 days each and you need 4, total time?",
                "Calculate: Grid system is 12 columns. Width of 8 columns as percentage?",
                "If usability score increased 35 points to 85, what was original score?"
        });
        uiux.put("Mixed", new String[]{
                "How would you redesign an app with poor user engagement?",
                "Explain your process for creating a design system from scratch.",
                "How do you incorporate user feedback into your designs?",
                "Describe your approach to mobile-first design.",
                "How do you ensure your designs are accessible to all users?",
                "What's your strategy for optimizing user onboarding experiences?",
                "How would you approach designing for an international audience?",
                "Explain how you measure the success of your designs.",
                "How do you handle conflicting stakeholder requirements in design?",
                "Describe a time when user testing revealed unexpected insights."
        });
        questionBank.put("UI/UX Designer", uiux);

        // QA/TESTER
        Map<String, String[]> qaTester = new HashMap<>();
        qaTester.put("HR Interview", new String[]{
                "What motivated you to choose QA testing as your career?",
                "Describe your most challenging bug and how you found it.",
                "How do you prioritize testing when time is limited?",
                "Tell me about your experience working with development teams.",
                "How do you stay motivated when testing repetitive scenarios?",
                "Describe a situation where you found a critical bug before release.",
                "How do you handle disagreements with developers about bugs?",
                "What's your approach to continuous learning in QA?"
        });
        qaTester.put("Technical", new String[]{
                "Explain the difference between manual and automated testing.",
                "What is the software testing life cycle?",
                "Describe black box, white box, and grey box testing.",
                "What types of testing are you familiar with? (unit, integration, system, UAT)",
                "How do you write effective test cases?",
                "What is regression testing? When do you perform it?",
                "Explain the bug life cycle.",
                "What testing tools and frameworks have you used? (Selenium, JUnit, TestNG)",
                "How do you perform API testing?",
                "What is test automation? Benefits and challenges?",
                "Explain the difference between smoke and sanity testing.",
                "What is exploratory testing?"
        });
        qaTester.put("Aptitude", new String[]{
                "100 test cases, 15 fail. What's the pass rate percentage?",
                "If testing 1 module takes 3 hours, time for 8 modules?",
                "Calculate: 500 bugs found, 425 fixed. What percentage remains?",
                "A test suite runs in 45 minutes. With parallelization it takes 15 minutes. Speed increase?",
                "If 85% tests pass and 120 tests run, how many passed?",
                "Calculate: 5 testers, 200 test cases, 2 days. Cases per tester per day?",
                "Pattern: 1, 4, 9, 16, 25, ?",
                "If bug detection rate is 8%, bugs expected in 2500 tests?",
                "Calculate: Automation reduces testing time by 60%. Original 10 hours, new time?",
                "If critical bugs are 12% of 250 total bugs, how many critical?",
                "Calculate: Manual test takes 30 minutes, automated takes 5 minutes. Time saved?",
                "If regression suite has 300 tests and you run 40%, how many tests?",
                "Calculate: Test execution time: 15 tests at 8 minutes each. Total?",
                "If test coverage increased from 70% to 90%, what's the increase?",
                "Calculate: 80 bugs found in sprint, 25% are duplicates. Unique bugs?"
        });
        qaTester.put("Mixed", new String[]{
                "How would you design a comprehensive test strategy for a new application?",
                "Explain your approach to performance and load testing.",
                "How do you ensure good communication between QA and development teams?",
                "What's your strategy for mobile app testing across different devices?",
                "How would you implement test automation for a large project?",
                "Describe your approach to security testing.",
                "How do you manage test data and test environments?",
                "What metrics do you use to measure testing effectiveness?",
                "How would you handle a situation where developers push back on bug reports?",
                "Explain your process for continuous testing in CI/CD pipelines."
        });
        questionBank.put("QA/Tester", qaTester);

        // AI ENGINEER
        Map<String, String[]> aiEngineer = new HashMap<>();
        aiEngineer.put("HR Interview", new String[]{
                "What inspired you to pursue a career in artificial intelligence?",
                "Describe an AI project you're most proud of.",
                "How do you approach ethical considerations in AI development?",
                "Tell me about your experience with machine learning research.",
                "How do you stay updated with rapidly evolving AI technologies?",
                "Describe a time when your AI model didn't perform as expected. How did you handle it?",
                "How do you explain complex AI concepts to non-technical stakeholders?",
                "What's your approach to balancing model accuracy with computational efficiency?"
        });
        aiEngineer.put("Technical", new String[]{
                "Explain the difference between supervised and unsupervised learning.",
                "What is deep learning? How does it differ from traditional ML?",
                "Describe the architecture of a neural network.",
                "What is overfitting? How do you prevent it?",
                "Explain backpropagation algorithm.",
                "What are common activation functions? When do you use each?",
                "How do CNNs work? When are they used?",
                "What is transfer learning? Give examples.",
                "Explain gradient descent and its variants.",
                "What is the bias-variance tradeoff?",
                "How do you evaluate model performance? Metrics you use?",
                "What is NLP? Describe transformers architecture."
        });
        aiEngineer.put("Aptitude", new String[]{
                "A model has 95% accuracy. How many correct from 2000 predictions?",
                "Calculate: Training takes 8 hours per epoch. Time for 15 epochs?",
                "If model size is 500MB and you compress 40%, what's new size?",
                "Calculate: Dataset has 10,000 samples, 70% training. Training samples?",
                "If precision is 85% and recall is 78%, estimate F1 score.",
                "Calculate: GPU costs $2/hour. Cost for 150 hours training?",
                "Pattern recognition: 2, 4, 16, 256, ?",
                "If inference time is 50ms per sample, time for 1000 samples?",
                "Calculate: Model achieves 88% accuracy, target is 95%. Gap?",
                "If batch size is 32 and dataset has 9600 samples, how many batches?",
                "Calculate: Learning rate starts at 0.01, decays 50% per epoch. Rate after 3 epochs?",
                "If error rate decreased from 15% to 6%, what's percentage reduction?",
                "Calculate: 5 layers, each with 100 neurons. Total neurons?",
                "If model parameters are 10 million, storage at 4 bytes each?",
                "Calculate: Confusion matrix shows 850 TP, 50 FP, 30 FN, 70 TN. Accuracy?"
        });
        aiEngineer.put("Mixed", new String[]{
                "How would you design an end-to-end ML pipeline for production?",
                "Explain your approach to feature engineering.",
                "How do you handle imbalanced datasets?",
                "Describe your strategy for model deployment and monitoring.",
                "What's your approach to AI model interpretability?",
                "How would you optimize model performance for edge devices?",
                "Explain your process for A/B testing ML models.",
                "How do you ensure fairness and reduce bias in AI models?",
                "Describe your experience with MLOps practices.",
                "How would you approach building a recommendation system?"
        });
        questionBank.put("AI Engineer", aiEngineer);
    }

    /**
     * Get a question based on job role, interview type, and question number
     */
    public static String getQuestion(String jobRole, String interviewType, int questionNumber) {
        Map<String, String[]> roleQuestions = questionBank.get(jobRole);

        if (roleQuestions == null) {
            return getFallbackQuestion(interviewType, questionNumber);
        }

        String[] questions = roleQuestions.get(interviewType);

        if (questions == null || questions.length == 0) {
            return getFallbackQuestion(interviewType, questionNumber);
        }

        // Use question number as index, or random if out of bounds
        int index;
        if (questionNumber <= questions.length) {
            index = questionNumber - 1;
        } else {
            index = random.nextInt(questions.length);
        }

        return questions[index];
    }

    /**
     * Generic fallback question
     */
    private static String getFallbackQuestion(String interviewType, int questionNumber) {
        String[] genericQuestions = {
                "Tell me about your relevant experience and skills.",
                "What are your key strengths for this position?",
                "Describe a challenging situation you've faced and how you handled it.",
                "How do you approach learning new technologies?",
                "What interests you most about this role?",
                "Describe a project you're particularly proud of.",
                "How do you handle working under pressure?",
                "What are your career goals for the next few years?",
                "How do you stay updated in your field?",
                "Tell me about a time you worked effectively in a team."
        };

        int index = (questionNumber - 1) % genericQuestions.length;
        return genericQuestions[index];
    }

    /**
     * Get random question for variety
     */
    public static String getRandomQuestion(String jobRole, String interviewType) {
        Map<String, String[]> roleQuestions = questionBank.get(jobRole);

        if (roleQuestions == null) {
            return getFallbackQuestion(interviewType, 1);
        }

        String[] questions = roleQuestions.get(interviewType);

        if (questions == null || questions.length == 0) {
            return getFallbackQuestion(interviewType, 1);
        }

        int randomIndex = random.nextInt(questions.length);
        return questions[randomIndex];
    }

    /**
     * Get all available job roles
     */
    public static String[] getAllJobRoles() {
        return questionBank.keySet().toArray(new String[0]);
    }

    /**
     * Get all interview types for a specific job role
     */
    public static String[] getInterviewTypes(String jobRole) {
        Map<String, String[]> roleQuestions = questionBank.get(jobRole);
        if (roleQuestions == null) {
            return new String[]{"HR Interview", "Technical", "Aptitude", "Mixed"};
        }
        return roleQuestions.keySet().toArray(new String[0]);
    }

    /**
     * Check if a specific combination exists
     */
    public static boolean hasQuestions(String jobRole, String interviewType) {
        Map<String, String[]> roleQuestions = questionBank.get(jobRole);
        if (roleQuestions == null) return false;

        String[] questions = roleQuestions.get(interviewType);
        return questions != null && questions.length > 0;
    }

    /**
     * Get total number of questions available for a role and type
     */
    public static int getQuestionCount(String jobRole, String interviewType) {
        Map<String, String[]> roleQuestions = questionBank.get(jobRole);
        if (roleQuestions == null) return 10;

        String[] questions = roleQuestions.get(interviewType);
        if (questions == null) return 10;

        return questions.length;
    }
}