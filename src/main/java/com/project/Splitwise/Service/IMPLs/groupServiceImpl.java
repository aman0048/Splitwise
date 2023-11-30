package com.project.Splitwise.Service.IMPLs;
import com.project.Splitwise.DTO.TransactionDTO;
import com.project.Splitwise.Exception.GroupNotFoundException;
import com.project.Splitwise.Models.Expense;
import com.project.Splitwise.Models.Group;
import com.project.Splitwise.Repository.GroupRepository;
import com.project.Splitwise.Service.GroupService;
import com.project.Splitwise.Service.Strategy.SettleUpStrategy;
import com.project.Splitwise.Service.Strategy.SettleUpStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class groupServiceImpl implements GroupService {
    @Autowired
    private GroupRepository groupRepository;

    @Override
    public List<TransactionDTO> settleUpByGroupId(int groupId) throws GroupNotFoundException {

        SettleUpStrategy strategy = SettleUpStrategyFactory.getSettleUpStrategy();
        Optional<Group> savedGroup = groupRepository.findById(groupId);
        if (savedGroup.isEmpty()) {
            throw new GroupNotFoundException("Group Not Found for the Given Group ID:" + groupId);
        }

        List<TransactionDTO> transactions = strategy.settleUp(savedGroup.get().getExpenses());
        return transactions;
    }

    //calculating the TotalAmount spend by a group
    @Override
    public double totalAmountSpentByUsers(int groupId) throws GroupNotFoundException {
        Group group=groupRepository.findById(groupId)
                .orElseThrow(()-> new GroupNotFoundException("The Group with "+groupId +" is not Present"));
        List<Expense>expenses=group.getExpenses();

        int totalAmt=0;
        for(Expense expense:expenses){//iterating through the list
            totalAmt+=expense.getAmount();
        }

        group.setTotalAmountSpend(totalAmt);
        groupRepository.save(group);
        return totalAmt;
    }
}
