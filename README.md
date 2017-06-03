# JsonAssert

## Idea

You have a class to be json serialized and want to check with rest-assured if the json data is correct.
Automatically generate some rest-assured jpath supported unittest code with support for nested json objects and lists. Based on given class.

## Usage

Look at the tests for examples.


## Known Drawbacks
 * The imports are a mess. spring-core is only required for RefletionUtils.
 * Java code generation.
 
