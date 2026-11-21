
# JoshHack Agent Instructions

## Project Environment
- JoshHack is a Java 17 Maven project.
- Use the Maven Wrapper for builds and testing.
- Production code is located in `src/main/java`.
- JUnit 5 tests are located in `src/test/java`.
- The project uses PIT mutation testing, SpotBugs static analysis, and GitHub Actions.

## Quality Verification
Before considering code or test changes complete:
1. Run the JUnit regression suite:
   `.\mvnw.cmd -B clean test`
2. Run PIT mutation testing:
   `.\mvnw.cmd -B test-compile org.pitest:pitest-maven:mutationCoverage`
3. Run SpotBugs static analysis:
   `.\mvnw.cmd -B spotbugs:spotbugs`
4. Review relevant coverage and analysis results.
5. Report any failing checks or checks that could not be executed.

## Development Rules
- Preserve existing application behavior unless a change is explicitly requested and approved.
- Never remove, weaken, or disable tests merely to make a build pass.
- Add meaningful, deterministic tests that verify observable behavior.
- Avoid flaky tests involving uncontrolled randomness.
- Keep modifications limited to the requested task.
- Clearly identify all proposed production-code changes and obtain human approval before making them.
- Do not modify build configuration or dependencies without approval.

## Git and Human Review
- Do not automatically commit, push, merge, or publish changes.
- Leave changes available for human inspection and approval.
- Review the Git diff and summarize all modified files.
- Report test results, coverage, mutation results, and remaining issues.
