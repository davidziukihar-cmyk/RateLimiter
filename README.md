## Application was created for study using AI in projects. 

### When someone will make pullRequest in GitHub of this project, AI will make review of that pull request.

**Use:**
 - GitHub Actions
 - some AI API_KEY (add GEMINI_API_KEY variable in GitHub secrets). I used free gemini api key, but it was often busy, so need to rerun workflow few times. And I add retries for Gemini API.

**GitHub Actions workflow steps(described in .github/workflows/ai-review.yml):**
 - actions/checkout@v5 - downloads repository code onto a workflow runner. It places project files in the $GITHUB_WORKSPACE directory so that later steps can build, test, or deploy our project.
 - actions/setup-java@v5 - setup Java
 - Run tests - run mvn test
 - Get PR diff - save PR in file pr.diff
 - Install Python dependencies (step, that prepares environment for running py -script in next step)
 - Send to AI reviewer - run py-script: python .github/scripts/ai_review.py

**Python script do:**
 - copy pr.diff content and send it with Prompt "Review the following Pull Request diff..." to AI agent.
 - post review comment

**That's all.**

To test workflow you need add commit and then make pull request. Check events on github/author/project/actions if someone goes wrong. If everything is OK, AI will add comment to your pull request.