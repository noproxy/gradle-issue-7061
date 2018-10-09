# Issue Demo

This is a sample project to reproduce issue-7061 of the gradle.(https://github.com/gradle/gradle/issues/7061).

## Project Structure

1. module 'library-xbundle' will produce a library with 'xbundle' format.
2. module 'issue-7061' will assemble a plugin that register some artifact transforms to produce issue.
3. the test in module 'issue-7061' will test the plugin, and test will fail to demonstrate the issue.
 

## Usage

Run this to running test. Test will fail.

```bash
./gradlew install test
```