package io.narayana.test.standalone;

public final class ApplicationServerMetadata {
    private String name, originalDistParam, jbossHome;

    private ApplicationServerMetadata() {}
    public static ApplicationServerMetadata instance() {
        return new ApplicationServerMetadata();
    }

    public String getJbossHome() {
        return jbossHome;
    }

    public ApplicationServerMetadata setJbossHome(String jbossHome) {
        this.jbossHome = jbossHome;
        return this;
    }

    public String getOriginalDistParam() {
        return originalDistParam;
    }

    public ApplicationServerMetadata setOriginalDistParam(String originalDistParam) {
        this.originalDistParam = originalDistParam;
        return this;
    }

    public String getName() {
        return name;
    }

    public ApplicationServerMetadata setName(String name) {
        this.name = name;
        return this;
    }
}
