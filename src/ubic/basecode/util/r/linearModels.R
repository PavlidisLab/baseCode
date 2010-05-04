c(rowlm<-function(formula=NULL,data) {
	if (is.null(formula)) {
		mf<-lm("~1",data,method="model.frame")
    	design<-matrix(1,ncol(data),1)
    	attr(design, "assign")<-0
	} else {
    	mf<-lm(formula,data,method="model.frame")
    	design<-model.matrix(formula)
    }
    mt <- attr(mf, "terms")
    x <- model.matrix(mt, mf)
    cl <- match.call()
    r<-nrow(data)
    res<-vector("list",r)
    lev<-.getXlevels(mt, mf)
    clz<-c("lm")
    D<-as.matrix(data)
    ids<-row.names(data)
    for(i in 1:r) {
        y<-as.vector(D[i,])
        id<-ids[i]
        m<-is.finite(y) 
        if (sum(m) > 0) {
            X<-design[m,,drop=FALSE]
            attr(X,"assign")<-attr(design,"assign")
            y<-y[m]
            z<-lm.fit(X,y)
            class(z) <- clz
            z$na.action <- na.exclude
            z$contrasts <- attr(x, "contrasts")
            z$xlevels <- lev
            z$call <- cl
            z$terms <- mt
            z$model <- mf
            res[[i]]<-z
        } 
    }
    names(res)<-row.names(data)
    return(res)
})
