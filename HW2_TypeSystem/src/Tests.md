# Testing

### Goal
- [X] ClassDeclaration (no main method) - Invalid
- [X] MainClass, 0 TypeDeclaration, \<EOF> - Valid
- [X] MainClass, 1 TypeDeclaration, \<EOF> - Valid
- [X] MainClass, >1 TypeDeclaration, \<EOF> - Valid


### Main class
- [X] 0 VarDeclaration(), 0 Statement() - Valid
- [X] 1 VarDeclaration(), 0 Statement() - Valid
- [X] >1 VarDeclaration(), 0 Statement() - Valid
- [ ] 0 Statement(), 1 VarDeclaration() - Valid
- [ ] 1 Statement(), 0 VarDeclaration() - Valid
- [ ] >1 Statement(), 0 VarDeclaration() - Valid
- [ ] 1 VarDeclaration(), 1 Statement() - Valid
- [ ] >1 VarDeclaration(), >1 Statement() - Valid

### TypeDeclaration
- [ ] 1 ClassDeclaration() - Valid
- [ ] 1 ClassExtendsDeclaration() - Valid
- [ ] 0 ClassDeclaration(), 0 ClassExtendsDeclaration - Valid


### ClassDeclaration
- [ ] Duplicate Identifier - Invalid
- [ ] 0 VarDeclaration, 0 MethodDeclaration
- [ ] 1 VarDeclaration, 1 MethodDeclaration
- [ ] Duplicate VarDeclaration
- [ ] Duplicate MethodDeclaration
- [ ] 