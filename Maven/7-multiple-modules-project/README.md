# Multiple Modules project in Maven 

It uses :
- Lombok project
- mapstruct to map commands to domain objects (and vice versa)
- jaxb to create domain/command object based on xsd (this should be split up in two modules)
- snapshot Central snapshot versioning (root) and propagate snapshot versions from root to downstream modules.

---
**Modules** includes :
- **entities** : to manage data access layer and the infrastructure
- **webapi/domain** : this should be split up between domain and commands (dto's)
- **Converter** : this insure a conversion type between entities, commands and domain model  
- **Webapp**: represent the application level


we added also `dependencyManagement` (i.e. **BOM** or bill of material) to manage the version of all downstream modules dependencies, i.e. jar's lib, except project/module dependencies such as jpa-entities, wep-api for converter project. Thus all defined **properties** inside the "down streams"  module pom.xml will be defined at root pom (i.e. at `properties` and `dependencyManagement` level). Therefore, versions of all modules dependencies will be inherited from `dependencyManagement` at root pom.xml.



