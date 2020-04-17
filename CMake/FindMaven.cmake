set (MAVEN_ROOT /usr/bin CACHE STRING "maven directory")

find_program(Maven_EXECUTABLE NAMES mvn
        HINTS ENV${MAVEN_ROOT}/mvn ${MAVEN_ROOT}/mvn)

include(FindPackageHandleStandardArgs)
find_package_handle_standard_args (Maven
  FOUND_VAR Maven_FOUND
  REQUIRED_VARS Maven_EXECUTABLE
)

mark_as_advanced(Maven_FOUND Maven_EXECUTABLE)
