````python
import os
import requests
from anthropic import Anthropic


MODEL = "claude-sonnet-4"


def read_file(path: str) -> str:
    if not os.path.exists(path):
        return ""

    with open(path, "r", encoding="utf-8", errors="ignore") as f:
        return f.read()


def build_prompt(diff: str) -> str:
    return f"""
You are a Senior Java and Spring code reviewer.

Review the following Pull Request diff.

Focus on:
- Bugs
- Concurrency issues
- Thread safety
- Spring transaction problems
- Security vulnerabilities
- Performance issues
- Missing tests
- Maintainability

Rules:
- Report only meaningful findings.
- Do not comment on formatting.
- Do not invent problems.
- Be concise.

Return Markdown.

PR diff:

```diff
{diff}
````

"""

def call_claude(prompt: str) -> str:
client = Anthropic(
api_key=os.environ["OPENAI_API_KEY"]
)

```
response = client.messages.create(
    model=MODEL,
    max_tokens=4000,
    messages=[
        {
            "role": "user",
            "content": prompt
        }
    ]
)

result = []

for block in response.content:
    if hasattr(block, "text"):
        result.append(block.text)

return "\n".join(result)
```

def post_pr_comment(comment: str) -> None:
token = os.environ["GITHUB_TOKEN"]
repository = os.environ["GITHUB_REPOSITORY"]
pr_number = os.environ["PR_NUMBER"]

```
url = (
    f"https://api.github.com/repos/"
    f"{repository}/issues/{pr_number}/comments"
)

response = requests.post(
    url,
    headers={
        "Authorization": f"Bearer {token}",
        "Accept": "application/vnd.github+json",
    },
    json={
        "body": comment
    },
    timeout=30,
)

response.raise_for_status()
```

def main():
diff = read_file("pr.diff")

```
if not diff.strip():
    print("No diff found.")
    return

print("Generating AI review...")

prompt = build_prompt(diff)

review = call_claude(prompt)

comment = f"""
```

## AI Code Review

{review}
"""

```
print("Posting review comment...")

post_pr_comment(comment)

print("Done.")
```

if **name** == "**main**":
main()

```
```
