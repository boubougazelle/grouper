delete from Category where subsystemID='testSubsystemId'
delete from Function_Permission where subsystemID='testSubsystemId'
delete from Function where subsystemID='testSubsystemId'
delete from Permission_Limit where subsystemID='testSubsystemId'
delete from Permission where subsystemID='testSubsystemId'
delete from Limit where subsystemID='testSubsystemId'
delete from Subsystem where subsystemID='testSubsystemId'
delete from Choice where choiceSetID like 'CHOICE_SET_%'
delete from ChoiceSet where choiceSetID like 'CHOICE_SET_%'
delete from Subject where subjectID like 'SUBJECT_%_ID'
delete from Tree where treeID = 'testTreeId'
delete from TreeNode where treeID = 'testTreeId'
delete from TreeNodeRelationship where treeID = 'testTreeId'
delete from Assignment where subsystemID='testSubsystemId'
delete from AssignmentLimitValue where limitID like 'LIMIT_%_ID'